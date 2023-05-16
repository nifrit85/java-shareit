package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserService mockUserService;

    private UserDto userDtoInput;
    private UserDto userDtoOutput;

    @BeforeEach
    void beforeEach() {
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
    void createTest() throws Exception {

        when(mockUserService.create(any(UserDto.class)))
                .thenReturn(userDtoOutput);
        //Создание
        String result = mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDtoInput))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Assertions.assertEquals(objectMapper.writeValueAsString(userDtoOutput), result);
        //Без почты
        userDtoInput.setEmail("");

        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDtoInput))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
        //Неверная почта
        userDtoInput.setEmail("qwe.ru");
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDtoInput))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
        //Без имени
        userDtoInput.setName(null);
        mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(userDtoInput))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Изменение пользователя")
    void updateTest() throws Exception {
        //Входные данные
        userDtoInput.setName("update");
        userDtoInput.setEmail("update@user.com");
        //Ожидаемые данные
        userDtoOutput.setName("update");
        userDtoOutput.setEmail("update@user.com");

        when(mockUserService.update(anyLong(), any(UserDto.class)))
                .thenReturn(userDtoOutput);
        //Изменение
        String result = mockMvc.perform(patch("/users/1")
                        .content(objectMapper.writeValueAsString(userDtoInput))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Assertions.assertEquals(objectMapper.writeValueAsString(userDtoOutput), result);
    }

    @Test
    @DisplayName("Получение пользователя")
    void getTest() throws Exception {
        when(mockUserService.get(anyLong()))
                .thenReturn(userDtoOutput);

        // Получение по Id
        String result = mockMvc.perform(get("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Assertions.assertEquals(objectMapper.writeValueAsString(userDtoOutput), result);

        //Получение всех
        //Ожидаемый результат
        List<UserDto> dtoList = List.of(userDtoOutput);

        when(mockUserService.get())
                .thenReturn(dtoList);

        result = mockMvc.perform(get("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Assertions.assertEquals(objectMapper.writeValueAsString(dtoList), result);
    }

    @Test
    @DisplayName("Удаление пользователя")
    void deleteTest() throws Exception {
        doNothing().when(mockUserService).delete(anyLong());

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
    }
}
