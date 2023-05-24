package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

public class ItemMapperTest {

    @Test
    void modelToDtoTest() {
        //Пользователь в репозитории
        User userInRepository = User.builder()
                .id(1L)
                .name("user")
                .email("user@user.com")
                .build();

        //Реквест в репозитории
        ItemRequest itemRequestInRepository = ItemRequest.builder()
                .id(1L)
                .description("Хотел бы воспользоваться дрелью")
                .requestor(userInRepository)
                .created(LocalDateTime.of(2023, 5, 9, 11, 20, 0))
                .build();

        //Вещь
        Item item = Item.builder()
                .id(1L)
                .name("Дрель")
                .description("Простая дрель")
                .available(true)
                .request(itemRequestInRepository)
                .build();

        ItemDto itemDto = ItemMapper.toDto(item);
        //Проверим что преобразование не вызвало изменений
        assertThat(itemDto)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", item.getName())
                .hasFieldOrPropertyWithValue("description", item.getDescription())
                .hasFieldOrPropertyWithValue("available", item.getAvailable())
                .hasFieldOrPropertyWithValue("lastBooking", null)
                .hasFieldOrPropertyWithValue("nextBooking", null)
                .hasFieldOrPropertyWithValue("comments", null)
                .hasFieldOrPropertyWithValue("requestId", 1L);
    }

    @Test
    void dtoToModel() {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Дрель")
                .description("Простая дрель")
                .available(true)
                .build();

        Item item = ItemMapper.toModel(itemDto);
        //Проверим что преобразование не вызвало изменений
        assertThat(item)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", itemDto.getName())
                .hasFieldOrPropertyWithValue("description", itemDto.getDescription())
                .hasFieldOrPropertyWithValue("available", itemDto.getAvailable())
                .hasFieldOrPropertyWithValue("owner", null)
                .hasFieldOrPropertyWithValue("request", null);
    }

    @Test
    void modelToShortDto(){

        //Пользователь в репозитории
        User userInRepository = User.builder()
                .id(1L)
                .name("user")
                .email("user@user.com")
                .build();

        //Реквест в репозитории
        ItemRequest itemRequestInRepository = ItemRequest.builder()
                .id(1L)
                .description("Хотел бы воспользоваться дрелью")
                .requestor(userInRepository)
                .created(LocalDateTime.of(2023, 5, 9, 11, 20, 0))
                .build();

        //Вещь
        Item item = Item.builder()
                .id(1L)
                .name("Дрель")
                .description("Простая дрель")
                .available(true)
                .request(itemRequestInRepository)
                .build();

        ItemShortDto itemShortDto = ItemMapper.toItemShortDto(item);
        //Проверим что преобразование не вызвало изменений
        assertThat(itemShortDto)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", item.getName())
                .hasFieldOrPropertyWithValue("description", item.getDescription())
                .hasFieldOrPropertyWithValue("available", item.getAvailable())
                .hasFieldOrPropertyWithValue("requestId", 1L);

    }
}
