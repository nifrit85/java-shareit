package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import ru.practicum.shareit.exception.NotFound;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
class UserServiceTest {
    @MockBean
    private UserRepository mockUserRepository;
    @Autowired
    private UserService userService;
    private User userInRepository;
    private UserDto userDtoInput;
    private UserDto userDtoOutput;

    @BeforeEach
    void beforeEach() {
        //Пользователь в репозитории
        userInRepository = User.builder()
                .id(1L)
                .name("user")
                .email("user@user.com")
                .build();
        //Входные данные
        userDtoInput = UserDto.builder()
                .name("user")
                .email("user@user.com")
                .build();
        //Ожидаемые данные
        userDtoOutput = UserDto.builder()
                .id(1L)
                .name("user")
                .email("user@user.com")
                .build();
    }

    @Test
    @DisplayName("Сохранение пользователя")
    void createTest() {
        when(mockUserRepository.save(any(User.class)))
                .thenReturn(userInRepository);
        //Создание
        assertEquals(userDtoOutput, userService.create(userDtoInput));
    }


    @Test
    @DisplayName("Изменение пользователя")
    void updateTest() {
        //Входные данные
        userDtoInput.setName("update");
        userDtoInput.setEmail("update@user.com");
        //Ожидаемые данные
        userDtoOutput.setName("update");
        userDtoOutput.setEmail("update@user.com");

        when(mockUserRepository.save(any(User.class)))
                .thenReturn(userInRepository);
        when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.of(userInRepository));
        //Изменение
        assertEquals(userDtoOutput, userService.update(2L, userDtoInput));
    }

    @Test
    @DisplayName("Получение пользователя")
    void getTest() {
        when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.of(userInRepository));
        //Получение по ID
        assertEquals(userDtoOutput, userService.get(1L));

        //Получение всех
        //Входные данные
        User user2 = User.builder()
                .id(2L)
                .name("user2")
                .email("user2@user.com")
                .build();

        List<User> users = new ArrayList<>();
        users.add(userInRepository);
        users.add(user2);

        //Ожидаемые данные
        List<UserDto> dtoList = users.stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());

        when(mockUserRepository.findAll())
                .thenReturn(users);
        assertEquals(dtoList, userService.get());
    }

    @Test
    @DisplayName("Удаление пользователя")
    void deleteTest() {
        when(mockUserRepository.existsById(anyLong()))
                .thenReturn(true);
        assertDoesNotThrow(() -> userService.delete(1L));
    }

    @Test
    @DisplayName("Проверка существования пользователя")
    void existsById() {
        when(mockUserRepository.existsById(anyLong()))
                .thenReturn(true);
        assertDoesNotThrow(() -> userService.existsById(1L));
    }

    @Test
    @DisplayName("Проверка исключения NotFound")
    void notFoundTest() {
        //Проверка изменения
        assertThrows(NotFound.class, () -> userService.update(1L, userDtoInput));
        //Проверка получения по id
        assertThrows(NotFound.class, () -> userService.get(1L));
        //Проверка перед удалением
        assertThrows(NotFound.class, () -> userService.delete(1L));
        //Проверка существования
        assertThrows(NotFound.class, () -> userService.existsById(1L));
    }
}
