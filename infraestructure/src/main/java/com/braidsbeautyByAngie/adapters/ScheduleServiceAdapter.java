package com.braidsbeautyByAngie.adapters;

import com.braidsbeautyByAngie.aggregates.constants.ReservationErrorEnum;
import com.braidsbeautyByAngie.aggregates.dto.ScheduleDTO;
import com.braidsbeautyByAngie.aggregates.request.RequestSchedule;
import com.braidsbeautyByAngie.aggregates.response.schedules.ResponseListPageableSchedule;
import com.braidsbeautyByAngie.aggregates.response.schedules.ResponseSchedule;
import com.braidsbeautyByAngie.aggregates.response.workService.ResponseWorkServiceWithoutReservation;
import com.braidsbeautyByAngie.aggregates.types.ScheduleStateEnum;
import com.braidsbeautyByAngie.entity.ScheduleEntity;
import com.braidsbeautyByAngie.mapper.ReservationMapper;
import com.braidsbeautyByAngie.mapper.ScheduleMapper;
import com.braidsbeautyByAngie.mapper.ServiceMapper;
import com.braidsbeautyByAngie.ports.out.ScheduleServiceOut;
import com.braidsbeautyByAngie.repository.ScheduleRepository;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.Constants;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.util.ValidateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleServiceAdapter implements ScheduleServiceOut {

    private final ScheduleRepository scheduleRepository;

    private final ScheduleMapper scheduleMapper;
    private final ServiceMapper serviceMapper;
    private final ReservationMapper reservationMapper;

    @Override
    public ScheduleDTO createScheduleOut(RequestSchedule requestSchedule) {
        log.info("Creating schedule with name: {}", "Test");

        ScheduleEntity scheduleEntity = ScheduleEntity.builder()
                .scheduleDate(requestSchedule.getScheduleDate())
                .scheduleHourStart(requestSchedule.getScheduleHourStart())
                .scheduleHourEnd(requestSchedule.getScheduleHourEnd())
                .employeeId(1L)
                .scheduleState(ScheduleStateEnum.FREE)
                .createdAt(Constants.getTimestamp())
                .state(Constants.STATUS_ACTIVE)
                .modifiedByUser("Test")
                .build();
        ScheduleEntity scheduleSaved = scheduleRepository.save(scheduleEntity);
        log.info("Schedule '{}' created successfully with ID: {}",scheduleSaved.getScheduleDate(),scheduleSaved.getScheduleId());
        return scheduleMapper.mapScheduleEntityToDTO(scheduleSaved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ResponseSchedule> findScheduleByIdOut(Long scheduleId) {
        log.info("Searching for schedule with ID: {}", scheduleId);

        // Verificar si el ScheduleEntity existe
        Optional<ScheduleEntity> optionalScheduleSaved = getScheduleEntity(scheduleId);
        if (optionalScheduleSaved.isEmpty()) {
            log.warn("Schedule with ID {} not found", scheduleId);
            return Optional.empty();
        }

            ResponseWorkServiceWithoutReservation responseWorkServiceWithoutReservation = ResponseWorkServiceWithoutReservation.builder()
                    .serviceDTOList(optionalScheduleSaved.get().getWorkServices().stream().map(workService -> serviceMapper.mapServiceEntityToDTO(workService.getServiceEntity())).collect(Collectors.toList()))
                    .reservationDTOList(optionalScheduleSaved.get().getWorkServices().stream().map(workService -> workService.getReservationEntity() != null ? reservationMapper.mapReservationEntityToDTO(workService.getReservationEntity()) : null).collect(Collectors.toList()))
                    .build();

        // Crear el ResponseSchedule con el mapeo correspondiente
        ResponseSchedule responseSchedule = ResponseSchedule.builder()
                .scheduleDTO(scheduleMapper.mapScheduleEntityToDTO(optionalScheduleSaved.get()))
                .responseWorkServiceWithoutReservation(responseWorkServiceWithoutReservation)
                .build();

        log.info("Schedule with ID {} found", scheduleId);
        return Optional.of(responseSchedule);
    }

    @Override
    public ScheduleDTO updateScheduleOut(Long scheduleId, RequestSchedule requestSchedule) {
        log.info("Searching for update schedule with ID: {}", scheduleId);
        ScheduleEntity scheduleSaved = scheduleRepository.findScheduleByIdWithStateTrue(scheduleId).orElse(null);
        if(scheduleSaved == null) {
            ValidateUtil.requerido(null, ReservationErrorEnum.SCHEDULE_NOT_FOUND_ERS00008, "Schedule not found with ID: " + scheduleId);
        }
        scheduleSaved.setScheduleDate(requestSchedule.getScheduleDate());
        scheduleSaved.setScheduleHourStart(requestSchedule.getScheduleHourStart());
        scheduleSaved.setScheduleHourEnd(requestSchedule.getScheduleHourEnd());
        scheduleSaved.setModifiedAt(Constants.getTimestamp());
        scheduleSaved.setModifiedByUser("TEST");

        ScheduleEntity scheduleUpdated = scheduleRepository.save(scheduleSaved);
        log.info("schedule updated with ID: {}", scheduleUpdated.getScheduleId());
        return scheduleMapper.mapScheduleEntityToDTO(scheduleUpdated);
    }

    @Override
    public ScheduleDTO deleteScheduleOut(Long scheduleId) {
        log.info("Searching schedule for delete with ID: {}", scheduleId);
        ScheduleEntity scheduleSaved = scheduleRepository.findScheduleByIdWithStateTrue(scheduleId).orElse(null);
        if(scheduleSaved == null) {
            ValidateUtil.requerido(null, ReservationErrorEnum.SCHEDULE_NOT_FOUND_ERS00008, "Schedule not found with ID: " + scheduleId);
        }
        scheduleSaved.setModifiedByUser("TEST-DELETED");
        scheduleSaved.setDeletedAt(Constants.getTimestamp());
        scheduleSaved.setState(Constants.STATUS_INACTIVE);
        scheduleSaved.setScheduleState(ScheduleStateEnum.CANCELLED);
        scheduleSaved = scheduleRepository.save(scheduleSaved);
        log.info("Schedule deleted with ID: {}", scheduleId);
        return scheduleMapper.mapScheduleEntityToDTO(scheduleSaved);
    }

    @Override
    public ResponseListPageableSchedule listScheduleByPageOut(int pageNumber, int pageSize, String orderBy, String sortDir) {
        log.info("Searching all schedules with the following parameters: {}", Constants.parametersForLogger(pageNumber, pageSize, orderBy, sortDir));

        // Configuración de ordenamiento y paginación
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(orderBy).ascending() : Sort.by(orderBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);

        // Obtención de la página de entidades de Schedule
        Page<ScheduleEntity> scheduleEntityPage = scheduleRepository.findAllByStateTrueAndPageable(pageable);

        // Retornar null si la página de entidades de Schedule está vacía
        if (scheduleEntityPage.isEmpty()) {
            return null;
        }

        // Mapeo de entidades de Schedule a ResponseSchedule
        List<ResponseSchedule> responseScheduleList = scheduleEntityPage.stream().map(scheduleEntity -> {

            // Manejo de posibles valores null para WorkServiceEntity y ServiceEntity
            ResponseWorkServiceWithoutReservation responseWorkServiceWithoutReservation = ResponseWorkServiceWithoutReservation.builder()
                    .serviceDTOList(scheduleEntity.getWorkServices().stream().map(workService -> serviceMapper.mapServiceEntityToDTO(workService.getServiceEntity())).collect(Collectors.toList()))
                    .reservationDTOList(scheduleEntity.getWorkServices().stream().map(workService -> workService.getReservationEntity() != null ? reservationMapper.mapReservationEntityToDTO(workService.getReservationEntity()) : null).collect(Collectors.toList()))
                    .build();

            return ResponseSchedule.builder()
                    .scheduleDTO(scheduleMapper.mapScheduleEntityToDTO(scheduleEntity))
                    .responseWorkServiceWithoutReservation(responseWorkServiceWithoutReservation)
                    .build();
        }).toList();

        // Creación de la respuesta paginada
        ResponseListPageableSchedule responseListPageableSchedule = ResponseListPageableSchedule.builder()
                .responseScheduleList(responseScheduleList)
                .pageNumber(scheduleEntityPage.getNumber())
                .totalElements(scheduleEntityPage.getTotalElements())
                .totalPages(scheduleEntityPage.getTotalPages())
                .pageSize(scheduleEntityPage.getSize())
                .end(scheduleEntityPage.isLast())
                .build();

        log.info("Schedules found with a total elements: {}", responseListPageableSchedule.getTotalElements());
        return responseListPageableSchedule;
    }

    private boolean scheduleExistsById(Long scheduleId){
        return scheduleRepository.existsByScheduleIdAndStateTrue(scheduleId);
    }
    private Optional<ScheduleEntity> getScheduleEntity(Long scheduleId){
        ScheduleEntity scheduleEntity = scheduleRepository.findScheduleByIdWithStateTrue(scheduleId).orElse(null);
        if(scheduleEntity == null) {
            return Optional.empty();
        }
        return Optional.of(scheduleEntity);
    }
}
