package ru.bookingsystem.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.bookingsystem.dto.request.RoomRequestDTO;
import ru.bookingsystem.entity.Room;

@Mapper(componentModel = "spring", uses = {EquipmentMapper.class})
public interface RoomMapper {
    Room toEntity(RoomRequestDTO roomRequest);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(RoomRequestDTO roomRequest, @MappingTarget Room entity);
}
