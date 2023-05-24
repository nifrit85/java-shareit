package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.booking.model.BookingRequestDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.booking.BookingStatus.APPROVED;
import static ru.practicum.shareit.booking.BookingStatus.WAITING;

@WebMvcTest(BookingController.class)
class BookingControllerTest {
    private static final String USER_ID = "X-Sharer-User-Id";
    private static final LocalDateTime START = LocalDateTime.of(2023, 6, 16, 13, 44, 30);
    private static final LocalDateTime END = LocalDateTime.of(2023, 7, 16, 13, 44, 30);
    private static final LocalDateTime SOME_DATE = LocalDateTime.of(2023, 5, 9, 11, 20, 0);
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private BookingService mockBookingService;

    private BookingRequestDto bookingRequestDtoInput;
    private BookingDto bookingDtoOutput;
    private User userInRepository;
    private User owner;
    private ItemRequest itemRequestInRepository;
    private Item itemInRepository;

    @BeforeEach
    void beforeEach() {
        //Пользователь в репозитории
        userInRepository = User.builder()
                .id(1L)
                .name("user")
                .email("user@user.com")
                .build();
        //Владелец в репозитории
        owner = User.builder()
                .id(2L)
                .name("owner")
                .email("owner@user.com")
                .build();
        //Реквест в репозитории
        itemRequestInRepository = ItemRequest.builder()
                .id(1L)
                .description("Хотел бы воспользоваться дрелью")
                .requestor(userInRepository)
                .created(SOME_DATE)
                .build();
        //Вещь в репозитории
        itemInRepository = Item.builder()
                .id(1L)
                .request(itemRequestInRepository)
                .owner(owner)
                .name("Дрель")
                .description("Простая дрель")
                .available(true)
                .build();
        //Входные данные
        bookingRequestDtoInput = BookingRequestDto.builder()
                .itemId(1L)
                .start(START)
                .end(END)
                .build();
        //Ожидаемые данные
        bookingDtoOutput = BookingDto.builder()
                .id(1L)
                .start(START)
                .end(END)
                .item(itemInRepository)
                .booker(userInRepository)
                .status(WAITING)
                .build();

    }

    @Test
    @DisplayName("Cоздание бронирования")
    void createTest() throws Exception {

        when(mockBookingService.create(any(BookingRequestDto.class), anyLong()))
                .thenReturn(bookingDtoOutput);

        //Создание
        String result = mockMvc.perform(post("/bookings")
                        .header(USER_ID, 1L)
                        .content(objectMapper.writeValueAsString(bookingRequestDtoInput))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        Assertions.assertEquals(objectMapper.writeValueAsString(bookingDtoOutput), result);

        //Пустая дата
        bookingRequestDtoInput.setStart(null);

        mockMvc.perform(post("/bookings")
                        .header(USER_ID, 1L)
                        .content(objectMapper.writeValueAsString(bookingRequestDtoInput))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());

        //Дата в прошлом
        bookingRequestDtoInput.setStart(LocalDateTime.now().minusWeeks(1));

        mockMvc.perform(post("/bookings")
                        .header(USER_ID, 1L)
                        .content(objectMapper.writeValueAsString(bookingRequestDtoInput))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Подверждение бронирования")
    void approveTest() throws Exception {
        bookingDtoOutput.setStatus(APPROVED);

        when(mockBookingService.approve(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingDtoOutput);

        //Подтверждение
        String result = mockMvc.perform(patch("/bookings/1")
                        .header(USER_ID, 1L)
                        .param("approved", "true")
                        .content(objectMapper.writeValueAsString(bookingRequestDtoInput))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        Assertions.assertEquals(objectMapper.writeValueAsString(bookingDtoOutput), result);
    }

    @Test
    @DisplayName("Получение бронирования")
    void getTest() throws Exception {
        when(mockBookingService.get(anyLong(),anyLong()))
                .thenReturn(bookingDtoOutput);

        //Получение по ID
        String result = mockMvc.perform(get("/bookings/1")
                        .header(USER_ID, 1L)
                        .content(objectMapper.writeValueAsString(bookingRequestDtoInput))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        Assertions.assertEquals(objectMapper.writeValueAsString(bookingDtoOutput), result);

        //Получение списка всех бронирований текущего пользователя
        List<BookingDto> bookingDtoList = new ArrayList<>();
        bookingDtoList.add(bookingDtoOutput);

        when(mockBookingService.get(anyString(),anyLong(),anyInt(),anyInt()))
                .thenReturn(bookingDtoList);

        result = mockMvc.perform(get("/bookings")
                        .header(USER_ID, 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        Assertions.assertEquals(objectMapper.writeValueAsString(bookingDtoList), result);

        //Получение списка бронирований для всех вещей текущего пользователя
        when(mockBookingService.getByOwner(anyString(),anyLong(),anyInt(),anyInt()))
                .thenReturn(bookingDtoList);

        result = mockMvc.perform(get("/bookings/owner")
                        .header(USER_ID, 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        Assertions.assertEquals(objectMapper.writeValueAsString(bookingDtoList), result);
    }
}
