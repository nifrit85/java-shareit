package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.model.CommentDto;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
class ItemControllerTest {
    private static final String USER_ID = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemService mockItemService;

    @Test
    void createTest() throws Exception {
        //Запрос для сохранения
        ItemDto itemDtoInput = ItemDto.builder()
                .id(1L)
                .name("Дрель")
                .description("Простая дрель")
                .available(true)
                .build();
        //Ожидаемый результат
        ItemDto itemDtoOutput = ItemDto.builder()
                .id(1L)
                .name("Дрель")
                .description("Простая дрель")
                .available(true)
                .build();

        when(mockItemService.create(any(ItemDto.class), anyLong()))
                .thenReturn(itemDtoOutput);

        //Создание
        String result = mockMvc.perform(post("/items")
                        .header(USER_ID, 1L)
                        .content(objectMapper.writeValueAsString(itemDtoInput))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        Assertions.assertEquals(objectMapper.writeValueAsString(itemDtoOutput), result);
        //Пустое наменование
        itemDtoInput.setName(null);
        mockMvc.perform(post("/items")
                        .header(USER_ID, 1L)
                        .content(objectMapper.writeValueAsString(itemDtoInput))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());

        //Пустое описание
        itemDtoInput.setName("Дрель");
        itemDtoInput.setDescription(null);
        mockMvc.perform(post("/items")
                        .header(USER_ID, 1L)
                        .content(objectMapper.writeValueAsString(itemDtoInput))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());

        //Неизвестная доступность
        itemDtoInput.setName("Дрель");
        itemDtoInput.setDescription("Простая дрель");
        itemDtoInput.setAvailable(null);
        mockMvc.perform(post("/items")
                        .header(USER_ID, 1L)
                        .content(objectMapper.writeValueAsString(itemDtoInput))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());

    }

    @Test
    void updateTest() throws Exception {
        //Входные данные
        ItemDto itemDtoInput = ItemDto.builder()
                .id(1L)
                .name("Дрель upd")
                .description("Простая дрель upd")
                .available(false)
                .build();

        //Ожидаемый результат
        ItemDto itemDtoOutput = ItemDto.builder()
                .id(1L)
                .name("Дрель upd")
                .description("Простая дрель upd")
                .available(false)
                .build();

        when(mockItemService.update(any(ItemDto.class), anyLong(), anyLong()))
                .thenReturn(itemDtoOutput);

        //Изменение
        String result = mockMvc.perform(patch("/items/1")
                        .header(USER_ID, 1L)
                        .content(objectMapper.writeValueAsString(itemDtoInput))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        Assertions.assertEquals(objectMapper.writeValueAsString(itemDtoOutput), result);
    }

    @Test
    void getTest() throws Exception {

        //Ожидаемый результат
        ItemDto itemDtoOutput = ItemDto.builder()
                .id(1L)
                .name("Дрель")
                .description("Простая дрель")
                .available(true)
                .build();

        Mockito.when(mockItemService.get(anyLong(), anyLong()))
                .thenReturn(itemDtoOutput);

        String result = mockMvc.perform(get("/items/1")
                        .header(USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        Assertions.assertEquals(objectMapper.writeValueAsString(itemDtoOutput), result);
    }

    @Test
    void getAll() throws Exception {

        //Ожидаемый результат
        ItemDto itemDtoOutput1 = ItemDto.builder()
                .id(1L)
                .name("Дрель")
                .description("Простая дрель")
                .available(true)
                .build();

        ItemDto itemDtoOutput2 = ItemDto.builder()
                .id(2L)
                .name("Дрель+")
                .description("Хорошая дрель")
                .available(true)
                .build();

        List<ItemDto> itemDtoList = new ArrayList<>();
        itemDtoList.add(itemDtoOutput1);
        itemDtoList.add(itemDtoOutput2);

        when(mockItemService.getAll(anyLong(), anyInt(), anyInt()))
                .thenReturn(itemDtoList);

        String result = mockMvc.perform(get("/items")
                        .header(USER_ID, 1L)
                        .param("from", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        Assertions.assertEquals(objectMapper.writeValueAsString(itemDtoList), result);
    }

    @Test
    void searchTest() throws Exception {
        //Ожидаемый результат
        ItemDto itemDtoOutput = ItemDto.builder()
                .id(1L)
                .name("Дрель")
                .description("Простая дрель")
                .available(true)
                .build();

        List<ItemDto> itemDtoList = new ArrayList<>();
        itemDtoList.add(itemDtoOutput);

        when(mockItemService.find(anyString(), anyInt(), anyInt()))
                .thenReturn(itemDtoList);

        String result = mockMvc.perform(get("/items/search")
                        .param("text", "Дрель")
                        .param("from", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        Assertions.assertEquals(objectMapper.writeValueAsString(itemDtoList), result);
    }

    @Test
    void deleteTest() throws Exception {
        doNothing().when(mockItemService).delete(anyLong(), anyLong());
        mockMvc.perform(delete("/items/1").header(USER_ID, 1L))
                .andExpect(status().isOk());
    }

    @Test
    void createCommentTest() throws Exception {

        //Входные данные
        CommentDto commentDtoInput = CommentDto.builder()
                .id(1L)
                .text("Отличный пылесос")
                .authorName("Василий")
                .created(LocalDateTime.now())
                .build();

        //Ожидаемый результат
        CommentDto commentDtoOutput = CommentDto.builder()
                .id(1L)
                .text("Отличный пылесос")
                .authorName("Василий")
                .created(LocalDateTime.now())
                .build();

        when(mockItemService.createComment(any(CommentDto.class), anyLong(), anyLong()))
                .thenReturn(commentDtoOutput);

        //Создание
        String result = mockMvc.perform(post("/items/1/comment")
                        .header(USER_ID, 1L)
                        .content(objectMapper.writeValueAsString(commentDtoInput))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        Assertions.assertEquals(objectMapper.writeValueAsString(commentDtoOutput), result);

        //Пустой текст
        commentDtoInput.setText("");

        mockMvc.perform(post("/items/1/comment")
                        .header(USER_ID, 1L)
                        .content(objectMapper.writeValueAsString(commentDtoInput))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }
}