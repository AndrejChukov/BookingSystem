package ru.bookingsystem.mapper;

import org.mapstruct.Mapper;
import ru.bookingsystem.dto.request.EquipmentRequestDTO;
import ru.bookingsystem.entity.Equipment;

@Mapper(componentModel = "spring")
public interface EquipmentMapper {
    Equipment toEntity(EquipmentRequestDTO equipmentRequest);
}
