package ru.practicum.shareit.booking.model;

import lombok.Data;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class BookingRequestDto {
    private Long itemId;
    @NotNull(message = "Дата не может быть пустой")
    @FutureOrPresent(message = "Дата не может быть в прошлом")
    private LocalDateTime start;
    @NotNull(message = "Дата не может быть пустой")
    @Future(message = "Дата не может быть в прошлом")
    private LocalDateTime end;
}
