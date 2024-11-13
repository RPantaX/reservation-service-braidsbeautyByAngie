package com.braidsbeautyByAngie.mapper;

import com.braidsbeautyByAngie.aggregates.dto.ReservationDTO;
import com.braidsbeautyByAngie.entity.ReservationEntity;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class ReservationMapper {
    private static final ModelMapper modelMapper = new ModelMapper();
    public ReservationDTO mapReservationEntityToDTO(ReservationEntity reservationEntity){
        return modelMapper.map(reservationEntity, ReservationDTO.class);
    }
    public ReservationEntity mapReservationDTOToEntity(ReservationDTO reservationDTO){
        return modelMapper.map(reservationDTO, ReservationEntity.class);
    }

}
