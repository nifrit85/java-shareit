package ru.practicum.shareit.request.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemShortDto;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

class ItemRequestMapperTest {
    @Test
    void modelToDtoTest() {

        User user = User.builder()
                .id(1L)
                .name("user")
                .email("user@user.com")
                .build();

        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .requestor(user)
                .created(LocalDateTime.now())
                .description("Хотел бы воспользоваться щёткой для обуви")
                .build();

        ItemShortDto itemShortDto1 = ItemShortDto.builder()
                .id(1L)
                .name("Белая щётка для обуви")
                .description("Щётка для обуви")
                .available(true)
                .requestId(1L)
                .build();
        ItemShortDto itemShortDto2 = ItemShortDto.builder()
                .id(2L)
                .name("Синяя щётка для обуви")
                .description("Хорошая щётка")
                .available(true)
                .requestId(1L)
                .build();

        List<ItemShortDto> itemShortDtoList = new ArrayList<>();
        itemShortDtoList.add(itemShortDto1);
        itemShortDtoList.add(itemShortDto2);

        ItemRequestDto itemRequestDto = ItemRequestMapper.toDto(itemRequest, itemShortDtoList);
        //Проверим что преобразование не вызвало изменений
        assertThat(itemRequestDto)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("description", itemRequest.getDescription())
                .hasFieldOrPropertyWithValue("created", itemRequest.getCreated())
                .hasFieldOrPropertyWithValue("items", itemShortDtoList);
    }

    @Test
    void dtoToModelTest() {
        LocalDateTime dateTime = LocalDateTime.now();

        User user = User.builder()
                .id(1L)
                .name("user")
                .email("user@user.com")
                .build();

        ItemRequest itemRequestBd = ItemRequest.builder()
                .id(1L)
                .requestor(user)
                .created(LocalDateTime.now())
                .description("Хотел бы воспользоваться щёткой для обуви")
                .build();

        Item item1 = Item.builder()
                .id(1L)
                .name("Белая щётка для обуви")
                .owner(user)
                .request(itemRequestBd)
                .available(true)
                .description("Щётка для обуви")
                .build();

        Item item2 = Item.builder()
                .id(2L)
                .name("Синяя щётка для обуви")
                .owner(user)
                .request(itemRequestBd)
                .available(true)
                .description("Хорошая щётка")
                .build();

        List<Item> itemList = new ArrayList<>();
        itemList.add(item1);
        itemList.add(item2);

        List<ItemShortDto> itemShortDtoList = itemList.stream()
                .map(ItemMapper::toItemShortDto)
                .collect(Collectors.toList());

        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("Хотел бы воспользоваться щёткой для обуви")
                .created(LocalDateTime.now())
                .items(itemShortDtoList)
                .build();

        ItemRequest itemRequest = ItemRequestMapper.toModel(itemRequestDto, dateTime, user);

        //Проверим что преобразование не вызвало изменений
        assertThat(itemRequest)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("description", itemRequestDto.getDescription())
                .hasFieldOrPropertyWithValue("requestor", user)
                .hasFieldOrPropertyWithValue("created", dateTime);
    }
}
