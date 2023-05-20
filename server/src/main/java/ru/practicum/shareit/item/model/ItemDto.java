package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.BookingShortDto;

import java.util.List;

@Data
@Builder
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingShortDto lastBooking;
    private BookingShortDto nextBooking;
    private List<CommentDto> comments;
    private Long requestId;
}
