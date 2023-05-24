package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.booking.model.BookingRequestDto;

import java.util.List;

public interface BookingService {
    BookingDto create(BookingRequestDto bookingDto, Long userId);

    BookingDto approve(Long bookingId, Long userId, Boolean approved);

    BookingDto get(Long bookingId, Long userId);

    List<BookingDto> get(String state, Long userId);

    List<BookingDto> getByOwner(String state, Long userId);
}
