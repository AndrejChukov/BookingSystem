package ru.bookingsystem.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.bookingsystem.dto.request.BookingRequestDTO;
import ru.bookingsystem.dto.response.BookingResponseDTO;
import ru.bookingsystem.entity.Booking;

@Mapper(componentModel = "spring")
public interface BookingMapper {

	@Mapping(target = "user", ignore = true)
	@Mapping(target = "room", ignore = true)
	@Mapping(target = "bookingStatus", ignore = true)
	Booking toEntity(BookingRequestDTO request);

	BookingResponseDTO toResponse(Booking booking);
}
