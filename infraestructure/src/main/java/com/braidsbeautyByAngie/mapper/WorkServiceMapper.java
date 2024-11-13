package com.braidsbeautyByAngie.mapper;

import com.braidsbeautyByAngie.aggregates.dto.WorkServiceDTO;
import com.braidsbeautyByAngie.entity.WorkServiceEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class WorkServiceMapper {
    private static final ModelMapper modelMapper = new ModelMapper();

    //mapToEntity
    public WorkServiceEntity mapWorkServiceDTOToEntity(WorkServiceDTO workServiceDTO){
        return modelMapper.map(workServiceDTO, WorkServiceEntity.class);
    }
    //mapToDto
    public WorkServiceDTO mapWorkServiceEntityToDTO(WorkServiceEntity workServiceEntity){
        return modelMapper.map(workServiceEntity, WorkServiceDTO.class);
    }

}
