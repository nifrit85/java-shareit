package ru.practicum.shareit.booking.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingRequestDto {
    private Long itemId;
    private LocalDateTime start;
    private LocalDateTime end;
}
