package com.braidsbeautyByAngie.adapters;

import com.braidsbeautyByAngie.aggregates.dto.ScheduleDTO;
import com.braidsbeautyByAngie.aggregates.request.RequestSchedule;
import com.braidsbeautyByAngie.aggregates.response.schedules.ResponseListPageableSchedule;
import com.braidsbeautyByAngie.aggregates.response.schedules.ResponseSchedule;
import com.braidsbeautyByAngie.aggregates.response.workService.ResponseWorkServiceWithoutReservation;
import com.braidsbeautyByAngie.entity.ScheduleEntity;
import com.braidsbeautyByAngie.entity.ServiceEntity;
import com.braidsbeautyByAngie.entity.WorkServiceEntity;
import com.braidsbeautyByAngie.mapper.ReservationMapper;
import com.braidsbeautyByAngie.mapper.ScheduleMapper;
import com.braidsbeautyByAngie.mapper.ServiceMapper;
import com.braidsbeautyByAngie.mapper.WorkServiceMapper;
import com.braidsbeautyByAngie.ports.out.ScheduleServiceOut;
import com.braidsbeautyByAngie.repository.ScheduleRepository;
import com.braidsbeautybyangie.coreservice.aggregates.Constants;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class ScheduleServiceAdapter implements ScheduleServiceOut {

    private final ScheduleRepository scheduleRepository;

    private final ScheduleMapper scheduleMapper;
    private final ServiceMapper serviceMapper;
    private final WorkServiceMapper workServiceMapper;
    private final ReservationMapper reservationMapper;
    private static final Logger logger = LoggerFactory.getLogger(ScheduleServiceAdapter.class);

    @Override
    public ScheduleDTO createScheduleOut(RequestSchedule requestSchedule) {
        logger.info("Creating schedule with name: {}", "Test");

        ScheduleEntity scheduleEntity = ScheduleEntity.builder()
                .scheduleDate(requestSchedule.getScheduleDate())
                .scheduleHourStart(requestSchedule.getScheduleHourStart())
                .scheduleHourEnd(requestSchedule.getScheduleHourEnd())
                .employeeId(1L)
                .scheduleState("LIBRE")
                .createdAt(Constants.getTimestamp())
                .state(Constants.STATUS_ACTIVE)
                .modifiedByUser("Test")
                .build();
        ScheduleEntity scheduleSaved = scheduleRepository.save(scheduleEntity);
        logger.info("Schedule '{}' created successfully with ID: {}",scheduleSaved.getScheduleDate(),scheduleSaved.getScheduleId());
        return scheduleMapper.mapScheduleEntityToDTO(scheduleSaved);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ResponseSchedule> findScheduleByIdOut(Long scheduleId) {
        logger.info("Searching for schedule with ID: {}", scheduleId);

        // Verificar si el ScheduleEntity existe
        Optional<ScheduleEntity> optionalScheduleSaved = getScheduleEntity(scheduleId);
        if (optionalScheduleSaved.isEmpty()) {
            logger.warn("Schedule with ID {} not found", scheduleId);
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

        logger.info("Schedule with ID {} found", scheduleId);
        return Optional.of(responseSchedule);
    }

    @Override
    public ScheduleDTO updateScheduleOut(Long scheduleId, RequestSchedule requestSchedule) {
        logger.info("Searching for update schedule with ID: {}", scheduleId);
        ScheduleEntity scheduleSaved = getScheduleEntity(scheduleId).get();

        scheduleSaved.setScheduleDate(requestSchedule.getScheduleDate());
        scheduleSaved.setScheduleHourStart(requestSchedule.getScheduleHourStart());
        scheduleSaved.setScheduleHourEnd(requestSchedule.getScheduleHourEnd());
        scheduleSaved.setModifiedAt(Constants.getTimestamp());
        scheduleSaved.setModifiedByUser("TEST");

        ScheduleEntity scheduleUpdated = scheduleRepository.save(scheduleSaved);
        logger.info("schedule updated with ID: {}", scheduleUpdated.getScheduleId());
        return scheduleMapper.mapScheduleEntityToDTO(scheduleUpdated);
    }

    @Override
    public ScheduleDTO deleteScheduleOut(Long scheduleId) {
        logger.info("Searching schedule for delete with ID: {}", scheduleId);
        ScheduleEntity scheduleSaved = getScheduleEntity(scheduleId).get();
        scheduleSaved.setModifiedByUser("TEST-DELETED");
        scheduleSaved.setDeletedAt(Constants.getTimestamp());
        scheduleSaved.setState(Constants.STATUS_INACTIVE);
        logger.info("Schedule deleted with ID: {}", scheduleId);
        return scheduleMapper.mapScheduleEntityToDTO(scheduleSaved);
    }

    @Override
    public ResponseListPageableSchedule listScheduleByPageOut(int pageNumber, int pageSize, String orderBy, String sortDir) {
        logger.info("Searching all schedules with the following parameters: {}", Constants.parametersForLogger(pageNumber, pageSize, orderBy, sortDir));

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

        logger.info("Schedules found with a total elements: {}", responseListPageableSchedule.getTotalElements());
        return responseListPageableSchedule;
    }

    private boolean scheduleExistsById(Long scheduleId){
        return scheduleRepository.existsByScheduleIdAndStateTrue(scheduleId);
    }
    private Optional<ScheduleEntity> getScheduleEntity(Long scheduleId){
        if (!scheduleExistsById(scheduleId)) throw new RuntimeException("The schedule does not exist.");
        return scheduleRepository.findScheduleByIdWithStateTrue(scheduleId);
    }
}
