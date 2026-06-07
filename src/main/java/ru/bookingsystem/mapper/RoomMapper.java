package ru.bookingsystem.mapper;

import org.mapstruct.Mapper;
import ru.bookingsystem.dto.request.RoomRequestDTO;
import ru.bookingsystem.entity.Room;

@Mapper(componentModel = "spring", uses = {EquipmentMapper.class})
public interface RoomMapper {
    Room toEntity(RoomRequestDTO roomRequest);
}
