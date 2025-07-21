package com.braidsbeautyByAngie.adapters;

import com.braidsbeautyByAngie.aggregates.constants.ReservationErrorEnum;
import com.braidsbeautyByAngie.aggregates.dto.ReservationDTO;
import com.braidsbeautyByAngie.aggregates.request.RequestReservation;
import com.braidsbeautyByAngie.aggregates.response.reservations.ResponseListPageableReservation;
import com.braidsbeautyByAngie.aggregates.response.reservations.ResponseReservation;
import com.braidsbeautyByAngie.aggregates.response.reservations.ResponseReservationDetail;
import com.braidsbeautyByAngie.aggregates.response.reservations.ResponseWorkServiceDetail;
import com.braidsbeautyByAngie.aggregates.response.workService.ResponseWorkService;

import com.braidsbeautyByAngie.aggregates.types.ReservationStateEnum;
import com.braidsbeautyByAngie.aggregates.types.ScheduleStateEnum;
import com.braidsbeautyByAngie.entity.*;

import com.braidsbeautyByAngie.mapper.ReservationMapper;
import com.braidsbeautyByAngie.mapper.ScheduleMapper;
import com.braidsbeautyByAngie.mapper.ServiceMapper;

import com.braidsbeautyByAngie.repository.*;
import com.braidsbeautyByAngie.ports.out.ReservationServiceOut;

import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.Constants;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.dto.ReservationCore;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.util.ValidateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
@Slf4j
public class ReservationServiceAdapter implements ReservationServiceOut {

    private final ReservationRepository reservationRepository;
    private final WorkServiceRepository workServiceRepository;
    private final ScheduleRepository scheduleRepository;
    private final ServiceRepository serviceRepository;

    private final ReservationMapper reservationMapper;
    private final ScheduleMapper scheduleMapper;
    private final ServiceMapper serviceMapper;

    private static final String STATUS_CREATED = "CREATED";
    private final PromotionRepository promotionRepository;

    @Override
    @Transactional
    public ReservationDTO createReservationOut(List<RequestReservation> requestReservationList) {
        log.info("Starting reservation creation for {} requests", requestReservationList.size());

        List<WorkServiceEntity> workServiceEntities = new ArrayList<>();

        try {
            // Primero, creamos la reserva sin los WorkServiceEntities
            ReservationEntity reservationEntity = ReservationEntity.builder()
                    .reservationState(ReservationStateEnum.CREATED)
                    .createdAt(Constants.getTimestamp())
                    .modifiedByUser(com.braidsbeautyByAngie.aggregates.constants.Constants.getUserInSession())
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

            //guardamos el monto total
            BigDecimal totalReservationPrice = calculateTotalReservation(workServiceEntityList);
            savedReservation.setReservationTotalPrice(totalReservationPrice);
            log.info("Total reservation price calculated: {}", totalReservationPrice);

            reservationRepository.save(savedReservation);

            log.info("Reservation created successfully with ID: {}", savedReservation.getReservationId());
            return reservationMapper.mapReservationEntityToDTO(savedReservation);
        } catch (Exception e) {
            log.error("Error occurred while creating reservation: {}", e.getMessage());
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
        log.info("Starting deletion process for reservation with ID: {}", reservationId);

        ReservationEntity reservationEntity = getReservationEntity(reservationId);
        for (ScheduleEntity scheduleEntity : reservationEntity.getWorkServiceEntities().stream().map(WorkServiceEntity::getScheduleEntity).toList()) {
            scheduleEntity.setScheduleState(ScheduleStateEnum.FREE);
            scheduleRepository.save(scheduleEntity);
        }
        reservationEntity.setReservationState(ReservationStateEnum.REJECTED);
        reservationEntity.setState(Constants.STATUS_INACTIVE);
        reservationEntity.setDeletedAt(Constants.getTimestamp());
        reservationEntity.setModifiedByUser(com.braidsbeautyByAngie.aggregates.constants.Constants.getUserInSession());

        ReservationEntity deletedReservation = reservationRepository.save(reservationEntity);

        log.info("Reservation with ID: {} marked as REJECTED", reservationId);
        return reservationMapper.mapReservationEntityToDTO(deletedReservation);
    }

    @Override
    public ResponseReservationDetail findReservationByIdOut(Long reservationId) {
        log.info("Searching for reservation with ID: {}", reservationId);
        ReservationEntity reservationSaved = getReservationEntity(reservationId);

        List<WorkServiceEntity> workServiceEntities = reservationSaved.getWorkServiceEntities();
        List <ResponseWorkServiceDetail> responseWorkServiceList = workServiceEntities.stream().map(workServiceEntity -> ResponseWorkServiceDetail.builder()
                    .serviceDTO(serviceMapper.mapServiceEntityToDTO(workServiceEntity.getServiceEntity()))
                    .scheduleDTO(scheduleMapper.mapScheduleEntityToDTO(workServiceEntity.getScheduleEntity()))
                    .build()).toList();
        ReservationDTO reservationDTO = reservationMapper.mapReservationEntityToDTO(reservationSaved);

        ResponseReservationDetail responseReservation =  ResponseReservationDetail.builder()
                .reservationId(reservationDTO.getReservationId())
                .reservationState(reservationDTO.getReservationState())
                .reservationTotalPrice(reservationDTO.getReservationTotalPrice())
                .responseWorkServiceDetails(responseWorkServiceList)
                .build();
        log.info("Reservation found: {}", reservationId);
        return responseReservation;
    }

    @Override
    public ResponseListPageableReservation listReservationByPageOut(int pageNumber, int pageSize, String orderBy, String sortDir) {
        log.info("Searching all promotions with the following parameters: {}", Constants.parametersForLogger(pageNumber, pageSize, orderBy, sortDir));
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
        log.info("Reservations found with the following parameters: {}", Constants.parametersForLogger(pageNumber, pageSize, orderBy, sortDir));
        return responseListPageableReservation;
    }

    @Override
    public ReservationCore reserveReservationOut(Long shopOrderId, Long reservationId) {

        ReservationEntity reservationEntity = reservationRepository.findReservationEntityByReservationIdAndReservationStateIsCreated(reservationId)
                .orElse(null);
        ValidateUtil.requerido(reservationEntity, ReservationErrorEnum.RESERVATION_NOT_FOUND_ERR00001, "Reservation is in progress or is rejected: " + reservationId);
        reservationEntity.setReservationState(ReservationStateEnum.APPROVED);
        reservationEntity.setShopOrderId(shopOrderId);
        reservationRepository.save(reservationEntity);

        return ReservationCore.builder()
                        .reservationId(reservationId)
                        .totalPrice(reservationEntity.getReservationTotalPrice())
                        .build();
    }
    @Override
    public void cancelReservationOut(Long reservationId) {
        ReservationEntity reservationEntity = reservationRepository.findReservationByReservationIdAndStateTrue(reservationId)
                .orElse(null);
        ValidateUtil.requerido(reservationEntity, ReservationErrorEnum.RESERVATION_NOT_FOUND_ERR00001, "Reservation does not exist or is inactive: " + reservationId);
        reservationEntity.setReservationState(ReservationStateEnum.CREATED);
        reservationRepository.save(reservationEntity);
    }
    private WorkServiceEntity createWorkServiceEntity(RequestReservation request, ReservationEntity reservationEntity) {
        ScheduleEntity scheduleEntity = scheduleRepository
                .findScheduleByIdWithStateTrueAndScheduleStateLIBRE(request.getScheduleId())
                .orElse(null);
        ValidateUtil.requerido(scheduleEntity, ReservationErrorEnum.SCHEDULE_NOT_FOUND_ERS00008,"Schedule not found for ID or Schedule Not Free: " + request.getScheduleId());
        scheduleEntity.setScheduleState(ScheduleStateEnum.RESERVED);
        ServiceEntity serviceEntity = serviceRepository
                .findServiceByIdWithStateTrue(request.getServiceId())
                .orElse(null);
        ValidateUtil.requerido(serviceEntity, ReservationErrorEnum.SERVICE_NOT_FOUND_ERS00015,"Service not found for ID: " + request.getServiceId());
        return WorkServiceEntity.builder()
                .scheduleEntity(scheduleEntity)
                .workServiceState(STATUS_CREATED)
                .serviceEntity(serviceEntity)
                .createdAt(Constants.getTimestamp())
                .state(Constants.STATUS_ACTIVE)
                .modifiedByUser(com.braidsbeautyByAngie.aggregates.constants.Constants.getUserInSession())
                .reservationEntity(reservationEntity)
                .build();
    }

    private boolean reservationExistsById(Long reservationId){
        return reservationRepository.existsReservationIdAndStateTrue(reservationId);
    }
    private ReservationEntity getReservationEntity(Long reservationId) {
        ReservationEntity reservationEntity = reservationRepository.findReservationByReservationIdAndStateTrue(reservationId).orElse(null);

        if (reservationEntity == null) {
            log.warn("Reservation with ID: {} does not exist or is inactive.", reservationId);
            ValidateUtil.requerido(null, ReservationErrorEnum.RESERVATION_NOT_FOUND_ERR00001, "The reservation does not exist or is inactive: " + reservationId);
        }
        return reservationEntity;
    }
    private BigDecimal calculateTotalReservation(List<WorkServiceEntity> workServiceEntities) {
        return workServiceEntities.stream()
                .map(workServiceEntity -> {
                    // Obtener el precio base del servicio
                    BigDecimal servicePrice = workServiceEntity.getServiceEntity().getServicePrice();

                    // Obtener las promociones asociadas al servicio
                    List<PromotionEntity> promotionEntityList = promotionRepository
                            .findPromotionsByServiceCategoryId(workServiceEntity.getServiceEntity().getServiceCategoryEntity().getServiceCategoryId());

                    // Ordenar los descuentos de menor a mayor
                    List<BigDecimal> sortedDiscountRates = promotionEntityList.stream()
                            .map(PromotionEntity::getPromotionDiscountRate)
                            .sorted() // Orden ascendente
                            .toList();

                    // Aplicar los descuentos en orden ascendente
                    BigDecimal discountedPrice = sortedDiscountRates.stream()
                            .reduce(servicePrice, (price, discountRate) -> price.subtract(price.multiply(discountRate)));

                    return discountedPrice;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add); // Sumamos todos los precios finales
    }
}
