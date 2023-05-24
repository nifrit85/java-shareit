package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {

    private Long id;
    @NotEmpty(message = "Комментарий не может быть пустым")
    private String text;

    private String authorName;

    private LocalDateTime created;
}
