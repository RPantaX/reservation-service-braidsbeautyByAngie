package com.braidsbeautyByAngie.adapters;

import com.braidsbeautyByAngie.aggregates.dto.ReservationDTO;
import com.braidsbeautyByAngie.aggregates.request.RequestReservation;
import com.braidsbeautyByAngie.aggregates.response.reservations.ResponseListPageableReservation;
import com.braidsbeautyByAngie.aggregates.response.reservations.ResponseReservation;
import com.braidsbeautyByAngie.aggregates.response.workService.ResponseWorkService;
import com.braidsbeautyByAngie.aggregates.response.workService.ResponseWorkServiceWithoutReservation;
import com.braidsbeautyByAngie.entity.ReservationEntity;
import com.braidsbeautyByAngie.entity.ScheduleEntity;
import com.braidsbeautyByAngie.entity.ServiceEntity;
import com.braidsbeautyByAngie.entity.WorkServiceEntity;
import com.braidsbeautyByAngie.mapper.ReservationMapper;
import com.braidsbeautyByAngie.mapper.ScheduleMapper;
import com.braidsbeautyByAngie.mapper.ServiceMapper;
import com.braidsbeautyByAngie.mapper.WorkServiceMapper;
import com.braidsbeautyByAngie.ports.out.ReservationServiceOut;
import com.braidsbeautyByAngie.repository.ReservationRepository;
import com.braidsbeautyByAngie.repository.ScheduleRepository;
import com.braidsbeautyByAngie.repository.ServiceRepository;
import com.braidsbeautyByAngie.repository.WorkServiceRepository;
import com.braidsbeautybyangie.coreservice.aggregates.Constants;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReservationServiceAdapter implements ReservationServiceOut {

    private final ReservationRepository reservationRepository;
    private final WorkServiceRepository workServiceRepository;
    private final ScheduleRepository scheduleRepository;
    private final ServiceRepository serviceRepository;

    private final ReservationMapper reservationMapper;
    private final WorkServiceMapper workServiceMapper;
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
            for (RequestReservation request : requestReservationList) {
                WorkServiceEntity workServiceEntity = createWorkServiceEntity(request);
                workServiceEntities.add(workServiceEntity);
            }

            workServiceRepository.saveAll(workServiceEntities);

            ReservationEntity reservationEntity = ReservationEntity.builder()
                    .reservationState(STATUS_CREATED)
                    .workServiceEntities(workServiceEntities)
                    .build();
            ReservationEntity savedReservation = reservationRepository.save(reservationEntity);

            logger.info("Reservation created successfully with ID: {}", savedReservation.getReservationId());
            return reservationMapper.mapReservationEntityToDTO(savedReservation);

        } catch (Exception e) {
            logger.error("Error occurred while creating reservation: {}", e.getMessage());
            throw new RuntimeException("Failed to create reservation", e);
        }
    }

    @Override
    public ReservationDTO updateReservationOut(Long reservationId, RequestReservation requestReservation) {
        return null;
    }

    @Override
    public ReservationDTO deleteReservationOut(Long reservationId) {
        return null;
    }

    @Override
    public Optional<ResponseReservation> findReservationByIdOut(Long reservationId) {
        logger.info("Searching for reservation with ID: {}", reservationId);
        Optional<ReservationEntity> reservationSaved = getReservationEntity(reservationId);
        List<WorkServiceEntity> workServiceEntities = reservationSaved.get().getWorkServiceEntities();
        List <ResponseWorkService> responseWorkServiceList = workServiceEntities.stream().map(workServiceEntity -> {
             return  ResponseWorkService.builder()
                    .serviceDTO(serviceMapper.mapServiceEntityToDTO(workServiceEntity.getServiceEntity()))
                    .scheduleDTO(scheduleMapper.mapScheduleEntityToDTO(workServiceEntity.getScheduleEntity()))
                    .build();
        }).toList();
        ResponseReservation responseReservation =  ResponseReservation.builder()
                .reservationDTO(reservationMapper.mapReservationEntityToDTO(reservationSaved.get()))
                .responseWorkServiceList(responseWorkServiceList)
                .build();
        logger.info("Reservation found: {}", reservationId);
        return Optional.of(responseReservation);
    }

    @Override
    public ResponseListPageableReservation listReservationByPageOut(int pageNumber, int pageSize, String orderBy, String sortDir) {
        return null;
    }

    private WorkServiceEntity createWorkServiceEntity(RequestReservation request) {
        ScheduleEntity scheduleEntity = scheduleRepository
                .findScheduleByIdWithStateTrue(request.getScheduleId())
                .orElseThrow(() -> new EntityNotFoundException("Schedule not found for ID: " + request.getScheduleId()));

        ServiceEntity serviceEntity = serviceRepository
                .findServiceByIdWithStateTrue(request.getServiceId())
                .orElseThrow(() -> new EntityNotFoundException("Service not found for ID: " + request.getServiceId()));

        return WorkServiceEntity.builder()
                .scheduleEntity(scheduleEntity)
                .serviceEntity(serviceEntity)
                .createdAt(Constants.getTimestamp())
                .state(Constants.STATUS_ACTIVE)
                .modifiedByUser(USER_CREATED)
                .build();
    }

    private boolean reservationExistsById(Long reservationId){
        return reservationRepository.existsReservationIdAndStateTrue(reservationId);
    }
    private Optional<ReservationEntity> getReservationEntity(Long reservationId){
        if(!reservationExistsById(reservationId)) throw new RuntimeException("The reservation does not exist.");
        return reservationRepository.findReservationByReservationIdAndStateTrue(reservationId);
    }
}
