package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.booking.model.BookingRequestDto;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotAvailable;
import ru.practicum.shareit.exception.NotFound;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.booking.BookingStatus.*;

@SpringBootTest
class BookingServiceTest {

    @MockBean
    private UserRepository mockUserRepository;
    @MockBean
    private ItemRepository mockItemRepository;
    @MockBean
    private BookingRepository mockBookingRepository;
    @Autowired
    private BookingService bookingService;

    private User userInRepository;
    private User owner;
    private Item itemInRepository;
    private Booking bookingInRepository;
    private BookingRequestDto bookingRequestDtoInput;
    private BookingDto bookingDtoOutput;
    private ItemRequest itemRequestInRepository;
    private static final LocalDateTime START = LocalDateTime.of(2023, 4, 16, 13, 44, 30);
    private static final LocalDateTime END = LocalDateTime.of(2023, 6, 16, 13, 44, 30);
    private static final LocalDateTime SOME_DATE = LocalDateTime.of(2023, 5, 9, 11, 20, 0);

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
        //Бронирование в репозитории
        bookingInRepository = Booking.builder()
                .id(1L)
                .start(START)
                .end(END)
                .item(itemInRepository)
                .booker(userInRepository)
                .status(WAITING)
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
    @DisplayName("Сохранение бронирования")
    void createTest() {
        when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.of(userInRepository));

        when(mockItemRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemInRepository));

        when(mockBookingRepository.save(any(Booking.class)))
                .thenReturn(bookingInRepository);
        //Создание
        assertEquals(bookingDtoOutput, bookingService.create(bookingRequestDtoInput, 1L));
    }

    @Test
    @DisplayName("Проверки перед созданием бронирования")
    void checkBeforeCreateTest() {
        //NotFound
        //Не найден пользовать
        assertThrows(NotFound.class, () -> bookingService.create(bookingRequestDtoInput, 1L));
        //Не найдена вещь
        when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.of(userInRepository));
        assertThrows(NotFound.class, () -> bookingService.create(bookingRequestDtoInput, 1L));
        //Нельзя бронировать свою вещь
        when(mockItemRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemInRepository));
        when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.of(owner));
        assertThrows(NotFound.class, () -> bookingService.create(bookingRequestDtoInput, 1L));
        //IllegalArgumentException
        //Дата окончания бронирования не может быть раньше даты начала бронирования
        bookingRequestDtoInput.setStart(END);
        bookingRequestDtoInput.setEnd(START);

        when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.of(userInRepository));
        when(mockItemRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemInRepository));
        assertThrows(IllegalArgumentException.class, () -> bookingService.create(bookingRequestDtoInput, 1L));
        //Дата окончания бронирования не может быть равна дате начала бронирования
        bookingRequestDtoInput.setStart(START);
        assertThrows(IllegalArgumentException.class, () -> bookingService.create(bookingRequestDtoInput, 1L));
        //NotAvailable
        //Вещь не доступна
        itemInRepository.setAvailable(false);
        bookingRequestDtoInput.setStart(START);
        bookingRequestDtoInput.setEnd(END);
        assertThrows(NotAvailable.class, () -> bookingService.create(bookingRequestDtoInput, 1L));
    }

    @Test
    @DisplayName("Подтверждения бронирования")
    void approveTest() {
        when(mockBookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(bookingInRepository));
        when(mockUserRepository.existsById(anyLong()))
                .thenReturn(true);
        when(mockBookingRepository.save(any(Booking.class)))
                .thenReturn(bookingInRepository);
        //Подтверждение
        assertEquals(APPROVED, bookingService.approve(1L, 2L, true).getStatus());
        //Отказ
        bookingInRepository.setStatus(WAITING);
        assertEquals(REJECTED, bookingService.approve(1L, 2L, false).getStatus());
    }

    @Test
    @DisplayName("Проверки перед подтверждением бронирования")
    void checkBeforeApproveTest() {
        when(mockBookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(bookingInRepository));
        when(mockBookingRepository.save(any(Booking.class)))
                .thenReturn(bookingInRepository);
        //NotFound
        //Не найден пользовать
        assertThrows(NotFound.class, () -> bookingService.approve(1L, 2L, false));
        //Подтверждаем не своё бронирование
        when(mockUserRepository.existsById(anyLong()))
                .thenReturn(true);
        assertThrows(NotFound.class, () -> bookingService.approve(1L, 1L, false));
        //IllegalArgumentException
        //Бронирование уже обработано
        bookingInRepository.setStatus(APPROVED);
        assertThrows(IllegalArgumentException.class, () -> bookingService.approve(1L, 2L, false));
    }

    @Test
    @DisplayName("Получение бронирования")
    void getTest() {
        when(mockBookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(bookingInRepository));
        when(mockUserRepository.existsById(anyLong()))
                .thenReturn(true);
        //Получение по ID
        assertEquals(bookingService.get(1L, 1L), bookingDtoOutput);
        //Получение списка бронирований
        List<Booking> bookingList = new ArrayList<>();
        bookingList.add(bookingInRepository);
        List<BookingDto> bookingDtoList = bookingList.stream()
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());

        when(mockBookingRepository.findAllByBookerId(anyLong(), any(Pageable.class)))
                .thenReturn(bookingList);
        when(mockBookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(bookingList);
        when(mockBookingRepository.findAllByBookerIdAndEndIsBefore(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(bookingList);
        when(mockBookingRepository.findAllByBookerIdAndStartIsAfter(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(bookingList);
        when(mockBookingRepository.findAllByBookerIdAndStatus(anyLong(), any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(bookingList);

        assertEquals(bookingDtoList, bookingService.get("ALL", 1L, 0, 10));
        assertEquals(bookingDtoList, bookingService.get("CURRENT", 1L, 0, 10));
        assertEquals(bookingDtoList, bookingService.get("PAST", 1L, 0, 10));
        assertEquals(bookingDtoList, bookingService.get("FUTURE", 1L, 0, 10));
        assertEquals(bookingDtoList, bookingService.get("WAITING", 1L, 0, 10));
        assertEquals(bookingDtoList, bookingService.get("REJECTED", 1L, 0, 10));
    }

    @Test
    @DisplayName("Проверки перед получением бронирования")
    void checkBeforeGetTest() {
        when(mockBookingRepository.findById(anyLong()))
                .thenReturn(Optional.of(bookingInRepository));
        ///NotFound
        //Не найден пользовать
        assertThrows(NotFound.class, () -> bookingService.get(1L, 1L));
        //Запрашивает не забронировавший и не хозяин
        when(mockUserRepository.existsById(anyLong()))
                .thenReturn(true);
        assertThrows(NotFound.class, () -> bookingService.get(1L, 3L));
    }

    @Test
    @DisplayName("Проверка ввода статуса бронирования")
    void checkValidStateTest() {
        when(mockUserRepository.existsById(anyLong()))
                .thenReturn(true);
        assertThrows(IllegalArgumentException.class, () -> bookingService.get("ELSE", 1L, 0, 10));
    }

    @Test
    @DisplayName("Получение списка бронирований по владельцу")
    void getByOwnerTest() {
        when(mockUserRepository.existsById(anyLong()))
                .thenReturn(true);
        //Получение списка бронирований
        List<Booking> bookingList = new ArrayList<>();
        bookingList.add(bookingInRepository);
        List<BookingDto> bookingDtoList = bookingList.stream()
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());

        when(mockBookingRepository.findAllByItemOwnerId(anyLong(), any(Pageable.class)))
                .thenReturn(bookingList);
        when(mockBookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(bookingList);
        when(mockBookingRepository.findAllByItemOwnerIdAndEndIsBefore(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(bookingList);
        when(mockBookingRepository.findAllByItemOwnerIdAndStartIsAfter(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(bookingList);
        when(mockBookingRepository.findAllByItemOwnerIdAndStatus(anyLong(), any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(bookingList);

        assertEquals(bookingDtoList, bookingService.getByOwner("ALL", 1L, 0, 10));
        assertEquals(bookingDtoList, bookingService.getByOwner("CURRENT", 1L, 0, 10));
        assertEquals(bookingDtoList, bookingService.getByOwner("PAST", 1L, 0, 10));
        assertEquals(bookingDtoList, bookingService.getByOwner("FUTURE", 1L, 0, 10));
        assertEquals(bookingDtoList, bookingService.getByOwner("WAITING", 1L, 0, 10));
        assertEquals(bookingDtoList, bookingService.getByOwner("REJECTED", 1L, 0, 10));
    }
}
