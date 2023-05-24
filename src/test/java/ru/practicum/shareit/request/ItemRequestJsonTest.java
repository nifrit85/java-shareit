package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.model.ItemShortDto;
import ru.practicum.shareit.request.model.ItemRequestDto;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestJsonTest {
    @Autowired
    private JacksonTester<ItemRequestDto> jacksonTester;

    @Test
    void dtoTest() throws IOException {

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


        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("Хотел бы воспользоваться щёткой для обуви")
                .created(LocalDateTime.of(2023, 5, 9, 15, 6, 1))
                .items(itemShortDtoList)
                .build();

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
}