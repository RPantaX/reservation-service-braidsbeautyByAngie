package com.braidsbeautyByAngie.adapters;

import com.braidsbeautyByAngie.aggregates.dto.ReservationDTO;
import com.braidsbeautyByAngie.aggregates.request.RequestReservation;
import com.braidsbeautyByAngie.aggregates.response.reservations.ResponseListPageableReservation;
import com.braidsbeautyByAngie.aggregates.response.reservations.ResponseReservation;
import com.braidsbeautyByAngie.aggregates.response.workService.ResponseWorkService;

import com.braidsbeautyByAngie.entity.ReservationEntity;
import com.braidsbeautyByAngie.entity.ScheduleEntity;
import com.braidsbeautyByAngie.entity.ServiceEntity;
import com.braidsbeautyByAngie.entity.WorkServiceEntity;

import com.braidsbeautyByAngie.mapper.ReservationMapper;
import com.braidsbeautyByAngie.mapper.ScheduleMapper;
import com.braidsbeautyByAngie.mapper.ServiceMapper;

import com.braidsbeautyByAngie.repository.ReservationRepository;
import com.braidsbeautyByAngie.repository.ScheduleRepository;
import com.braidsbeautyByAngie.repository.ServiceRepository;
import com.braidsbeautyByAngie.repository.WorkServiceRepository;
import com.braidsbeautyByAngie.ports.out.ReservationServiceOut;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.braidsbeautybyangie.coreservice.aggregates.Constants;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
public class ReservationServiceAdapter implements ReservationServiceOut {

    private final ReservationRepository reservationRepository;
    private final WorkServiceRepository workServiceRepository;
    private final ScheduleRepository scheduleRepository;
    private final ServiceRepository serviceRepository;

    private final ReservationMapper reservationMapper;
    private final ScheduleMapper scheduleMapper;
    private final ServiceMapper serviceMapper;

    private static final Logger logger = LoggerFactory.getLogger(ReservationServiceAdapter.class);

    private static final String USER_CREATED = "USER-CREATED";
    private static final String STATUS_CREATED = "CREADO";

    @Override
    @Transactional
    public ReservationDTO createReservationOut(List<RequestReservation> requestReservationList) {
        logger.info("Starting reservation creation for {} requests", requestReservationList.size());

        List<WorkServiceEntity> workServiceEntities = new ArrayList<>();

        try {
            // Primero, creamos la reserva sin los WorkServiceEntities
            ReservationEntity reservationEntity = ReservationEntity.builder()
                    .reservationState(STATUS_CREATED)
                    .createdAt(Constants.getTimestamp())
                    .modifiedByUser(USER_CREATED)
                    .state(Constants.STATUS_ACTIVE)
                    .build();

            // Guardamos la reserva para obtener su ID
            ReservationEntity savedReservation = reservationRepository.save(reservationEntity);

            // Ahora, asociamos cada WorkServiceEntity con la reserva recién creada
            for (RequestReservation request : requestReservationList) {
                WorkServiceEntity workServiceEntity = createWorkServiceEntity(request, savedReservation);
                workServiceEntities.add(workServiceEntity);
            }

            // Guardamos las WorkServiceEntities asociadas a la reserva
            List<WorkServiceEntity> workServiceEntityList = workServiceRepository.saveAll(workServiceEntities);

            // Finalmente, asociamos los WorkServiceEntities con la reserva y la guardamos
            savedReservation.setWorkServiceEntities(workServiceEntityList);
            reservationRepository.save(savedReservation);

            logger.info("Reservation created successfully with ID: {}", savedReservation.getReservationId());
            return reservationMapper.mapReservationEntityToDTO(savedReservation);
        } catch (Exception e) {
            logger.error("Error occurred while creating reservation: {}", e.getMessage());
            throw new RuntimeException("Failed to create reservation", e);
        }
    }

    @Override
    public ReservationDTO updateReservationOut(Long reservationId, List<RequestReservation> requestReservationList) {
        //las reservaciones solo se pueden eliminar
        return null;
    }
    /**
     * Marca una reservación como cancelada.
     *
     * @param reservationId ID de la reservación a cancelar
     * @return ReservationDTO de la reservación cancelada
     */
    @Override
    @Transactional
    public ReservationDTO deleteReservationOut(Long reservationId) {
        logger.info("Starting deletion process for reservation with ID: {}", reservationId);

        ReservationEntity reservationEntity = getReservationEntity(reservationId)
                .orElseThrow(() -> new EntityNotFoundException("Reservation not found for ID: " + reservationId));
        for (ScheduleEntity scheduleEntity : reservationEntity.getWorkServiceEntities().stream().map(WorkServiceEntity::getScheduleEntity).toList()) {
            scheduleEntity.setScheduleState("LIBRE");
            scheduleRepository.save(scheduleEntity);
        }
        reservationEntity.setReservationState("CANCELADO");
        reservationEntity.setState(Constants.STATUS_INACTIVE);
        reservationEntity.setDeletedAt(Constants.getTimestamp());
        reservationEntity.setModifiedByUser("USER-MODIFIED");

        ReservationEntity deletedReservation = reservationRepository.save(reservationEntity);

        logger.info("Reservation with ID: {} marked as CANCELADO", reservationId);
        return reservationMapper.mapReservationEntityToDTO(deletedReservation);
    }

    @Override
    public Optional<ResponseReservation> findReservationByIdOut(Long reservationId) {
        logger.info("Searching for reservation with ID: {}", reservationId);
        Optional<ReservationEntity> reservationSaved = getReservationEntity(reservationId);
        List<WorkServiceEntity> workServiceEntities = reservationSaved.get().getWorkServiceEntities();
        List <ResponseWorkService> responseWorkServiceList = workServiceEntities.stream().map(workServiceEntity -> ResponseWorkService.builder()
                    .serviceDTO(serviceMapper.mapServiceEntityToDTO(workServiceEntity.getServiceEntity()))
                    .scheduleDTO(scheduleMapper.mapScheduleEntityToDTO(workServiceEntity.getScheduleEntity()))
                    .build()).toList();
        ResponseReservation responseReservation =  ResponseReservation.builder()
                .reservationDTO(reservationMapper.mapReservationEntityToDTO(reservationSaved.get()))
                .responseWorkServiceList(responseWorkServiceList)
                .build();
        logger.info("Reservation found: {}", reservationId);
        return Optional.of(responseReservation);
    }

    @Override
    public ResponseListPageableReservation listReservationByPageOut(int pageNumber, int pageSize, String orderBy, String sortDir) {
        logger.info("Searching all promotions with the following parameters: {}", Constants.parametersForLogger(pageNumber, pageSize, orderBy, sortDir));
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(orderBy).ascending() : Sort.by(orderBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        if(reservationRepository.findAllByStateTrueAndPageable(pageable).isEmpty()) return null;

        Page<ReservationEntity> reservationEntityPage = reservationRepository.findAllByStateTrueAndPageable(pageable);

        List<ResponseReservation> responseReservationList = reservationEntityPage.getContent().stream().map(reservation -> {
            List<WorkServiceEntity> workServiceEntities = reservation.getWorkServiceEntities();
            List<ResponseWorkService> responseWorkServiceList = workServiceEntities.stream().map(workServiceEntity -> ResponseWorkService.builder()
                    .serviceDTO(serviceMapper.mapServiceEntityToDTO(workServiceEntity.getServiceEntity()))
                    .scheduleDTO(scheduleMapper.mapScheduleEntityToDTO(workServiceEntity.getScheduleEntity()))
                    .build()).toList();
            return ResponseReservation.builder()
                    .reservationDTO(reservationMapper.mapReservationEntityToDTO(reservation))
                    .responseWorkServiceList(responseWorkServiceList)
                    .build();
        }).toList();

        ResponseListPageableReservation responseListPageableReservation = ResponseListPageableReservation.builder()
                .responseReservationList(responseReservationList)
                .pageNumber(reservationEntityPage.getNumber())
                .totalElements(reservationEntityPage.getTotalElements())
                .totalPages(reservationEntityPage.getTotalPages())
                .pageSize(reservationEntityPage.getSize())
                .end(reservationEntityPage.isLast())
                .build();
        logger.info("Reservations found with the following parameters: {}", Constants.parametersForLogger(pageNumber, pageSize, orderBy, sortDir));
        return responseListPageableReservation;
    }

    private WorkServiceEntity createWorkServiceEntity(RequestReservation request, ReservationEntity reservationEntity) {
        ScheduleEntity scheduleEntity = scheduleRepository
                .findScheduleByIdWithStateTrueAndScheduleStateLIBRE(request.getScheduleId())
                .orElseThrow(() -> new EntityNotFoundException("Schedule not found for ID or Schedule Not Free: " + request.getScheduleId()));
        scheduleEntity.setScheduleState("RESERVADO");
        ServiceEntity serviceEntity = serviceRepository
                .findServiceByIdWithStateTrue(request.getServiceId())
                .orElseThrow(() -> new EntityNotFoundException("Service not found for ID: " + request.getServiceId()));

        return WorkServiceEntity.builder()
                .scheduleEntity(scheduleEntity)
                .workServiceState(STATUS_CREATED)
                .serviceEntity(serviceEntity)
                .createdAt(Constants.getTimestamp())
                .state(Constants.STATUS_ACTIVE)
                .modifiedByUser(USER_CREATED)
                .reservationEntity(reservationEntity)
                .build();
    }

    private boolean reservationExistsById(Long reservationId){
        return reservationRepository.existsReservationIdAndStateTrue(reservationId);
    }
    private Optional<ReservationEntity> getReservationEntity(Long reservationId) {
        if (!reservationExistsById(reservationId)) {
            logger.warn("Reservation with ID: {} does not exist or is inactive.", reservationId);
            throw new EntityNotFoundException("The reservation does not exist or is inactive.");
        }
        return reservationRepository.findReservationByReservationIdAndStateTrue(reservationId);
    }
}
