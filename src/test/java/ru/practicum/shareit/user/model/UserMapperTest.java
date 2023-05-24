package ru.practicum.shareit.user.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.UserMapper;

import static org.assertj.core.api.Assertions.assertThat;


class UserMapperTest {
    @Test
    void modelToDtoTest() {

        User user = User.builder()
                .id(1L)
                .name("user")
                .email("user@user.com")
                .build();

        UserDto userDto = UserMapper.toDto(user);
        //Проверим что преобразование не вызвало изменений
        assertThat(userDto)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name",user.getName())
                .hasFieldOrPropertyWithValue("email",user.getEmail());
    }

    @Test
    void dtoToModelTest(){
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("user")
                .email("user@user.com")
                .build();

        User user = UserMapper.toModel(userDto);
        //Проверим что преобразование не вызвало изменений
        Assertions.assertThat(user)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name",userDto.getName())
                .hasFieldOrPropertyWithValue("email",userDto.getEmail());

    }
}
