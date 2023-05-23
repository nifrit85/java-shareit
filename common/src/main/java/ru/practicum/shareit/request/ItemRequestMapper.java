package ru.practicum.shareit.request;

import ru.practicum.shareit.item.model.ItemShortDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public class ItemRequestMapper {
    private ItemRequestMapper() {
        throw new IllegalStateException("Utility class");
    }

    public static ItemRequestDto toDto(ItemRequest itemRequest, List<ItemShortDto> items) {
        return ItemRequestDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .items(items)
                .build();
    }

    public static ItemRequest toModel(ItemRequestDto itemRequestDto, LocalDateTime created, User user) {
        return ItemRequest.builder()
                .id(itemRequestDto.getId())
                .description(itemRequestDto.getDescription())
                .requestor(user)
                .created(created)
                .build();
    }
}
