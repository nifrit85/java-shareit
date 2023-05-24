package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class CommentMapperTest {

    @Test
    void modelToDtoTest() {

        //Пользователь
        User user = User.builder()
                .id(1L)
                .name("user")
                .email("user@user.com")
                .build();

        //Вещь
        Item item = Item.builder()
                .id(1L)
                .name("Пылесос")
                .description("Мощный пылесос")
                .available(true)
                .build();

        Comment comment = Comment.builder()
                .id(1L)
                .text("Отличный пылесос")
                .item(item)
                .author(user)
                .created(LocalDateTime.now())
                .build();

        CommentDto commentDto = CommentMapper.toDto(comment);
        //Проверим что преобразование не вызвало изменений
        assertThat(commentDto)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("text", comment.getText())
                .hasFieldOrPropertyWithValue("authorName", user.getName())
                .hasFieldOrPropertyWithValue("created", comment.getCreated());
    }

    @Test
    void dtoToModel() {
        //Пользователь
        User user = User.builder()
                .id(1L)
                .name("user")
                .email("user@user.com")
                .build();

        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("Отличный пылесос")
                .authorName(user.getName())
                .created(LocalDateTime.now())
                .build();

        //Вещь
        Item item = Item.builder()
                .id(1L)
                .name("Пылесос")
                .description("Мощный пылесос")
                .available(true)
                .build();

        Comment comment = CommentMapper.toModel(commentDto,user,item);
        //Проверим что преобразование не вызвало изменений
        assertThat(comment)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("text", commentDto.getText())
                .hasFieldOrPropertyWithValue("item", item)
                .hasFieldOrPropertyWithValue("author", user);
    }
}
