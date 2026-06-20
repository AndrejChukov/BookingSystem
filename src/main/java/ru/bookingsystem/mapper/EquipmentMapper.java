package ru.bookingsystem.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.bookingsystem.dto.request.EquipmentRequestDTO;
import ru.bookingsystem.dto.response.EquipmentResponseDTO;
import ru.bookingsystem.entity.Equipment;

@Mapper(componentModel = "spring")
public interface EquipmentMapper {
    Equipment toEntity(EquipmentRequestDTO equipmentRequest);

    EquipmentResponseDTO equipmentToResponse(Equipment equipment);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(EquipmentRequestDTO equipmentRequest, @MappingTarget Equipment entity);
}
