package com.braidsbeautyByAngie.mapper;


import com.braidsbeautyByAngie.aggregates.dto.ServiceDTO;
import com.braidsbeautyByAngie.entity.ServiceEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class ServiceMapper {

    private static final ModelMapper modelMapper = new ModelMapper();

    //maptoDto
    public ServiceDTO mapServiceEntityToDTO(ServiceEntity serviceEntity){
        return modelMapper.map(serviceEntity, ServiceDTO.class);
    }
    //maptoEntity
    public ServiceEntity mapServiceDTOToEntity(ServiceDTO serviceDTO){
        return modelMapper.map(serviceDTO, ServiceEntity.class);
    }

}
