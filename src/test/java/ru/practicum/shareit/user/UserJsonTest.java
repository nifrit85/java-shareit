package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.user.model.UserDto;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserJsonTest {
    @Autowired
    private JacksonTester<UserDto> jacksonTester;

    private UserDto userDto;

    @BeforeEach
    void beforeEach() {
        userDto = UserDto.builder()
                .id(1L)
                .name("user")
                .email("user@user.com")
                .build();
    }

    @Test
    @DisplayName("Сериализация User")
    void testSerialize() throws IOException {
        JsonContent<UserDto> json = jacksonTester.write(userDto);
        //Проверим сериализацию
        assertThat(json).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(json).extractingJsonPathStringValue("$.name").isEqualTo("user");
        assertThat(json).extractingJsonPathStringValue("$.email").isEqualTo("user@user.com");
    }

    @Test
    @DisplayName("Десериализация User")
    void testDeserialize() throws IOException {
        String userString = "{\"id\":\"1\",\"name\":\"user\",\"email\":\"user@user.com\"}";

        //Проверим десериализацию
        assertThat(jacksonTester.parse(userString)).isEqualTo(userDto);
    }
}
