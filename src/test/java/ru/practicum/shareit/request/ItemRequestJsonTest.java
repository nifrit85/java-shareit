package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.model.ItemShortDto;
import ru.practicum.shareit.request.model.ItemRequestDto;
import ru.practicum.shareit.user.model.UserDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestJsonTest {
    @Autowired
    private JacksonTester<ItemRequestDto> jacksonTester;

    private ItemShortDto itemShortDto1;
    private ItemShortDto itemShortDto2;
    private ItemRequestDto itemRequestDto;


    @BeforeEach
    void beforeEach() {
        itemShortDto1 = ItemShortDto.builder()
                .id(1L)
                .name("Белая щётка для обуви")
                .description("Щётка для обуви")
                .available(true)
                .requestId(1L)
                .build();
        itemShortDto2 = ItemShortDto.builder()
                .id(2L)
                .name("Синяя щётка для обуви")
                .description("Хорошая щётка")
                .available(true)
                .requestId(1L)
                .build();

        List<ItemShortDto> itemShortDtoList = new ArrayList<>();
        itemShortDtoList.add(itemShortDto1);
        itemShortDtoList.add(itemShortDto2);


        itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("Хотел бы воспользоваться щёткой для обуви")
                .created(LocalDateTime.of(2023, 5, 9, 15, 6, 1))
                .items(itemShortDtoList)
                .build();
    }

    @Test
    @DisplayName("Сериализация ItemRequest")
    void testSerialize() throws IOException {

        JsonContent<ItemRequestDto> json = jacksonTester.write(itemRequestDto);
        //Проверим что преобразование не вызвало изменений
        assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("$.description").isEqualTo("Хотел бы воспользоваться щёткой для обуви");
        assertThat(json).extractingJsonPathStringValue("$.created").isEqualTo(itemRequestDto.getCreated().toString());
        assertThat(json).extractingJsonPathValue("$.items[0]").extracting("id").isEqualTo(1);
        assertThat(json).extractingJsonPathValue("$.items[0]").extracting("name").isEqualTo(itemShortDto1.getName());
        assertThat(json).extractingJsonPathValue("$.items[0]").extracting("description").isEqualTo(itemShortDto1.getDescription());
        assertThat(json).extractingJsonPathValue("$.items[0]").extracting("available").isEqualTo(itemShortDto1.getAvailable());
        assertThat(json).extractingJsonPathValue("$.items[0]").extracting("requestId").isEqualTo(1);
        assertThat(json).extractingJsonPathValue("$.items[1]").extracting("id").isEqualTo(2);
        assertThat(json).extractingJsonPathValue("$.items[1]").extracting("name").isEqualTo(itemShortDto2.getName());
        assertThat(json).extractingJsonPathValue("$.items[1]").extracting("description").isEqualTo(itemShortDto2.getDescription());
        assertThat(json).extractingJsonPathValue("$.items[1]").extracting("available").isEqualTo(itemShortDto2.getAvailable());
        assertThat(json).extractingJsonPathValue("$.items[1]").extracting("requestId").isEqualTo(1);
    }

    @Test
    @DisplayName("ItemRequest")
    void testDeserialize() throws IOException {
        String itemRequestString = "{\"id\":1,\"description\":\"Хотел бы воспользоваться щёткой для обуви\",\"created\":\"2023-05-09T15:06:01\",\"items\":[{\"id\":1,\"name\":\"Белая щётка для обуви\",\"description\":\"Щётка для обуви\",\"available\":true,\"requestId\":1},{\"id\":2,\"name\":\"Синяя щётка для обуви\",\"description\":\"Хорошая щётка\",\"available\":true,\"requestId\":1}]}";

        //Проверим десериализацию
        assertThat(jacksonTester.parse(itemRequestString)).isEqualTo(itemRequestDto);
    }
}
