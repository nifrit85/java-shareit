package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
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

    @Test
    void createTest() throws Exception {

        //Пользователь для сохранения
        UserDto userDtoInput = UserDto.builder()
                .name("user")
                .email("user@user.com")
                .build();

        //Ожидаемый результат
        UserDto userDtoOutput = UserDto.builder()
                .id(1L)
                .name("user")
                .email("user@user.com")
                .build();

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
    void updateTest() throws Exception {
        //Пользователь для изменения
        UserDto userDtoInput = UserDto.builder()
                .id(1L)
                .name("updated")
                .email("updated@user.com")
                .build();

        //Ожидаемый результат
        UserDto userDtoOutput = UserDto.builder()
                .id(1L)
                .name("updated")
                .email("updated@user.com")
                .build();

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
    void getTest() throws Exception {
        //Ожидаемый результат
        UserDto userDtoOutput = UserDto.builder()
                .id(1L)
                .name("user")
                .email("user@user.com")
                .build();

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
    void deleteTest() throws Exception {
        doNothing().when(mockUserService).delete(anyLong());

        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
    }
}
