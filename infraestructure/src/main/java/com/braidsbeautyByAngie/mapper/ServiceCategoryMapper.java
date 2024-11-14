package com.braidsbeautyByAngie.mapper;

import com.braidsbeautyByAngie.aggregates.dto.ServiceCategoryDTO;
import com.braidsbeautyByAngie.aggregates.dto.ServiceDTO;
import com.braidsbeautyByAngie.entity.ServiceCategoryEntity;
import com.braidsbeautyByAngie.entity.ServiceEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class ServiceCategoryMapper {

    private static final ModelMapper modelMapper = new ModelMapper();

    public ServiceCategoryEntity mapServiceDTOToEntity(ServiceCategoryDTO serviceCategoryDTO){
        return modelMapper.map(serviceCategoryDTO, ServiceCategoryEntity.class);
    }
    public ServiceCategoryDTO mapServiceEntityToDTO(ServiceCategoryEntity serviceCategoryEntity){
        return modelMapper.map(serviceCategoryEntity, ServiceCategoryDTO.class);
    }

}
