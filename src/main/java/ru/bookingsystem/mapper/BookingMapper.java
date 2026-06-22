package ru.bookingsystem.mapper;

import ru.bookingsystem.dto.request.BookingRequestDTO;
import ru.bookingsystem.dto.response.BookingResponseDTO;
import ru.bookingsystem.entity.Booking;

public interface BookingMapper {

	Booking toEntity(BookingRequestDTO request);

	BookingResponseDTO toResponse(Booking booking);
}
