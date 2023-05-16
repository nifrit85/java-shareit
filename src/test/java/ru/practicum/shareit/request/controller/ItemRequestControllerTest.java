package ru.practicum.shareit.request.controller;

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
import ru.practicum.shareit.item.model.ItemShortDto;
import ru.practicum.shareit.request.model.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {
    private static final String USER_ID = "X-Sharer-User-Id";
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemRequestService mockItemRequestService;

    private ItemRequestDto itemRequestDtoInput;
    private ItemRequestDto itemRequestDtoOutput;

    @BeforeEach
    void beforeEach() {
        //Входные данные
        itemRequestDtoInput = ItemRequestDto.builder()
                .description("Хотел бы воспользоваться щёткой для обуви")
                .build();
        //Ожидаемые данные
        itemRequestDtoOutput = ItemRequestDto.builder()
                .id(1L)
                .description("Хотел бы воспользоваться щёткой для обуви")
                .created(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("Сохранение реквеста")
    void createTest() throws Exception {
        when(mockItemRequestService.create(any(ItemRequestDto.class), anyLong()))
                .thenReturn(itemRequestDtoOutput);

        //Создание
        String result = mockMvc.perform(post("/requests")
                        .header(USER_ID, 1L)
                        .content(objectMapper.writeValueAsString(itemRequestDtoInput))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        Assertions.assertEquals(objectMapper.writeValueAsString(itemRequestDtoOutput), result);
        //Пустое описание
        itemRequestDtoInput.setDescription(null);
        mockMvc.perform(post("/requests")
                        .header(USER_ID, 1L)
                        .content(objectMapper.writeValueAsString(itemRequestDtoInput))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Получение реквеста")
    void getTest() throws Exception {

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

        ItemRequestDto itemRequestDtoFirst = ItemRequestDto.builder()
                .id(1L)
                .description("Хотел бы воспользоваться щёткой для обуви")
                .created(LocalDateTime.now())
                .items(itemShortDtoList)
                .build();

        ItemRequestDto itemRequestDtoSecond = ItemRequestDto.builder()
                .id(2L)
                .description("Хотел бы воспользоваться пылесосом")
                .created(LocalDateTime.now())
                .build();

        List<ItemRequestDto> itemRequestDtoList = new ArrayList<>();
        itemRequestDtoList.add(itemRequestDtoFirst);
        itemRequestDtoList.add(itemRequestDtoSecond);

        when(mockItemRequestService.get(anyLong()))
                .thenReturn(itemRequestDtoList);

        //Получение списка своих реквестов
        String result = mockMvc.perform(get("/requests")
                        .header(USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        Assertions.assertEquals(objectMapper.writeValueAsString(itemRequestDtoList), result);

        //Получение всех реквестов пользователя

        when(mockItemRequestService.get(anyLong(), anyInt(), anyInt()))
                .thenReturn(itemRequestDtoList);

        result = mockMvc.perform(get("/requests/all")
                        .header(USER_ID, 1L)
                        .param("from", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        Assertions.assertEquals(objectMapper.writeValueAsString(itemRequestDtoList), result);

        //Получение конкретного реквеста
        when(mockItemRequestService.get(anyLong(), anyLong()))
                .thenReturn(itemRequestDtoFirst);

        result = mockMvc.perform(get("/requests/1")
                        .header(USER_ID, 1L)
                        .param("requestId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        Assertions.assertEquals(objectMapper.writeValueAsString(itemRequestDtoFirst), result);

    }
}
