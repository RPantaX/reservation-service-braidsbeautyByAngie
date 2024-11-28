package com.braidsbeautyByAngie.adapters;

import com.braidsbeautyByAngie.aggregates.dto.WorkServiceDTO;
import com.braidsbeautyByAngie.aggregates.response.workService.ResponseListPageableWorkService;
import com.braidsbeautyByAngie.aggregates.response.workService.ResponseWorkService;
import com.braidsbeautyByAngie.entity.WorkServiceEntity;
import com.braidsbeautyByAngie.mapper.ReservationMapper;
import com.braidsbeautyByAngie.mapper.ScheduleMapper;
import com.braidsbeautyByAngie.mapper.ServiceMapper;
import com.braidsbeautyByAngie.mapper.WorkServiceMapper;
import com.braidsbeautyByAngie.ports.out.WorkServiceServiceOut;
import com.braidsbeautyByAngie.repository.WorkServiceRepository;
import com.braidsbeautybyangie.sagapatternspringboot.aggregates.aggregates.Constants;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkServiceAdapter implements WorkServiceServiceOut {

    private final WorkServiceRepository workServiceRepository;

    private final WorkServiceMapper workServiceMapper;
    private final ServiceMapper serviceMapper;
    private final ScheduleMapper scheduleMapper;
    private final ReservationMapper reservationMapper;

    @Override
    public ResponseListPageableWorkService listWorkServiceByPageOut(int pageNumber, int pageSize, String orderBy, String sortDir) {
        log.info("Searching all work-services with the following parameters: {}", Constants.parametersForLogger(pageNumber, pageSize, orderBy, sortDir));
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(orderBy).ascending() : Sort.by(orderBy).descending();
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        if(workServiceRepository.findAllByStateTrueAndPageable(pageable).isEmpty()) return null;
        Page<WorkServiceEntity> workServicePage = workServiceRepository.findAllByStateTrueAndPageable(pageable);

        List<ResponseWorkService> responseWorkServiceList = workServicePage.getContent().stream().map(workServiceEntity -> ResponseWorkService.builder()
                .serviceDTO(serviceMapper.mapServiceEntityToDTO(workServiceEntity.getServiceEntity()))
                .scheduleDTO(scheduleMapper.mapScheduleEntityToDTO(workServiceEntity.getScheduleEntity()))
                .reservationDTO(reservationMapper.mapReservationEntityToDTO(workServiceEntity.getReservationEntity()))
                .build()).toList();

        ResponseListPageableWorkService responseListPageableWorkService = ResponseListPageableWorkService.builder()
                .responseWorkServiceList(responseWorkServiceList)
                .pageNumber(workServicePage.getNumber())
                .totalElements(workServicePage.getTotalElements())
                .totalPages(workServicePage.getTotalPages())
                .pageSize(workServicePage.getSize())
                .end(workServicePage.isLast())
                .build();

        log.info("work-services found with a total elements: {}", workServicePage.getTotalElements());
        return responseListPageableWorkService;
    }

    @Override
    public WorkServiceDTO cancelWorkServiceOut(Long workServiceId) {
        log.info("Searching work-service for cancel with ID: {}", workServiceId);

        WorkServiceEntity workServiceSaved = getWorkServiceEntity(workServiceId).get();
        workServiceSaved.setWorkServiceState("CANCELLED");

        WorkServiceEntity workServiceCanceled = workServiceRepository.save(workServiceSaved);
        log.info("work-service canceled with ID: {}", workServiceCanceled.getWorkServiceId());
        return workServiceMapper.mapWorkServiceEntityToDTO(workServiceCanceled);
    }

    private boolean workServiceExistsById(Long workId){
        return workServiceRepository.existsByWorkServiceIdAndStateTrue(workId);
    }

    private Optional<WorkServiceEntity> getWorkServiceEntity(Long workId){
        if(!workServiceExistsById(workId)) throw new RuntimeException("tHE work service does not exist.");
        return workServiceRepository.findWorkServiceByIdWithStateTrue(workId);
    }
}
