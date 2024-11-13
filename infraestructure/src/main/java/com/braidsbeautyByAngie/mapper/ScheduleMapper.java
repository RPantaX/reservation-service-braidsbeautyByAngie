package com.braidsbeautyByAngie.mapper;

import com.braidsbeautyByAngie.aggregates.dto.ScheduleDTO;
import com.braidsbeautyByAngie.entity.ScheduleEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class ScheduleMapper {

    private static final ModelMapper modelMapper = new ModelMapper();

    public ScheduleEntity mapScheduleDTOToEntity(ScheduleDTO scheduleDTO){
        return modelMapper.map(scheduleDTO, ScheduleEntity.class);
    }
    public ScheduleDTO mapScheduleEntityToDTO(ScheduleEntity scheduleEntity){
        return modelMapper.map(scheduleEntity, ScheduleDTO.class);
    }
}
