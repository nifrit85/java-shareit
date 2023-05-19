package ru.practicum.shareit.request.model;


import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.model.ItemShortDto;
import ru.practicum.shareit.marker.Marker;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ItemRequestDto {
    private Long id;
    @NotBlank(groups = Marker.OnCreate.class)
    private String description;
    private LocalDateTime created;
    private List<ItemShortDto> items;
}
