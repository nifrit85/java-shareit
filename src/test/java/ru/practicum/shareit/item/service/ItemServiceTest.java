package ru.practicum.shareit.item.service;

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
import ru.practicum.shareit.booking.model.BookingShortDto;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFound;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.booking.BookingStatus.APPROVED;

@SpringBootTest
class ItemServiceTest {
    private static final LocalDateTime SOME_DATE = LocalDateTime.of(2023, 5, 9, 11, 20, 0);
    @MockBean
    private ItemRepository mockItemRepository;
    @Autowired
    private ItemService itemService;
    @MockBean
    private UserRepository mockUserRepository;
    @MockBean
    private ItemRequestRepository mockItemRequestRepository;
    @MockBean
    private CommentRepository mockCommentRepository;
    @MockBean
    private BookingRepository mockBookingRepository;
    private User userInRepository;
    private ItemRequest itemRequestInRepository;
    private ItemDto itemDtoInput;
    private ItemDto itemDtoOutput;

    private Item itemInRepository;
    private Item itemInRepository2;

    @BeforeEach
    void beforeEach() {
        //Пользователь в репозитории
        userInRepository = User.builder()
                .id(1L)
                .name("user")
                .email("user@user.com")
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
                .owner(userInRepository)
                .name("Дрель")
                .description("Простая дрель")
                .available(true)
                .build();

        itemInRepository2 = Item.builder()
                .id(2L)
                .request(itemRequestInRepository)
                .owner(userInRepository)
                .name("Дрель++")
                .description("Мощная дрель")
                .available(true)
                .build();

        //Входные данные
        itemDtoInput = ItemDto.builder()
                .name("Дрель")
                .description("Простая дрель")
                .available(true)
                .requestId(1L)
                .build();
        //Ожидаемые данные
        itemDtoOutput = ItemDto.builder()
                .id(1L)
                .name("Дрель")
                .description("Простая дрель")
                .available(true)
                .requestId(1L)
                .build();

    }

    @Test
    @DisplayName("Сохранение вещи")
    void createTest() {
        when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.of(userInRepository));
        when(mockItemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemRequestInRepository));
        when(mockItemRepository.save(any(Item.class)))
                .thenReturn(itemInRepository);
        //Создание
        assertEquals(itemDtoOutput, itemService.create(itemDtoInput, userInRepository.getId()));
    }

    @Test
    @DisplayName("Изменение вещи")
    void updateTest() {
        itemInRepository.setName("updated");
        itemInRepository.setDescription("updated");
        //Входные данные
        itemDtoInput.setName("updated");
        itemDtoInput.setDescription("updated");
        //Ожидаемые данные
        itemDtoOutput.setName("updated");
        itemDtoOutput.setDescription("updated");

        when(mockUserRepository.existsById(anyLong()))
                .thenReturn(true);
        when(mockItemRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemInRepository));
        when(mockItemRepository.save(any(Item.class)))
                .thenReturn(itemInRepository);

        assertEquals(itemDtoOutput, itemService.update(itemDtoInput, 1L, userInRepository.getId()));
    }

    @Test
    @DisplayName("Получение вещи")
    void getTest() {
        //Коментарии в репозитории
        Comment commentInRepository1 = Comment.builder()
                .id(1L)
                .text("text")
                .author(userInRepository)
                .item(itemInRepository)
                .created(LocalDateTime.now())
                .build();

        Comment commentInRepository2 = Comment.builder()
                .id(2L)
                .text("text2")
                .author(userInRepository)
                .item(itemInRepository)
                .created(LocalDateTime.now())
                .build();

        List<Comment> commentList = new ArrayList<>();
        commentList.add(commentInRepository1);
        commentList.add(commentInRepository2);

        List<CommentDto> commentDtoList = commentList.
                stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList());

        //Ожидаемые данные
        itemDtoOutput.setComments(commentDtoList);


        when(mockUserRepository.existsById(anyLong()))
                .thenReturn(true);
        when(mockItemRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemInRepository));
        when(mockCommentRepository.findAllByItemIdIn(anyList(), any()))
                .thenReturn(commentList);

        //Без бронирования
        assertEquals(itemDtoOutput, itemService.get(1L, 2L));

        //Бронирования в репозитории
        Booking bookingInRepository1 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusWeeks(1L))
                .end(LocalDateTime.now().plusWeeks(2L))
                .item(itemInRepository)
                .booker(userInRepository)
                .status(APPROVED)
                .build();

        Booking bookingInRepository2 = Booking.builder()
                .id(2L)
                .start(LocalDateTime.now().minusWeeks(2L))
                .end(LocalDateTime.now().minusWeeks(1L))
                .item(itemInRepository)
                .booker(userInRepository)
                .status(APPROVED)
                .build();

        List<Booking> bookingList = new ArrayList<>();
        bookingList.add(bookingInRepository1);
        bookingList.add(bookingInRepository2);

        BookingShortDto bookingShortDto1 = BookingMapper.toShortDto(bookingInRepository1);
        BookingShortDto bookingShortDto2 = BookingMapper.toShortDto(bookingInRepository2);

        //Ожидаемые данные
        itemDtoOutput.setLastBooking(bookingShortDto2);
        itemDtoOutput.setNextBooking(bookingShortDto1);

        when(mockBookingRepository.findAllByItemInAndStatus(anyList(), any(BookingStatus.class)))
                .thenReturn(bookingList);
        when(mockItemRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemInRepository));

        //Получение по ID
        assertEquals(itemDtoOutput, itemService.get(1L, 1L));
        //Получение без проверок
        assertEquals(itemInRepository, itemService.get(1L));
    }

    @Test
    @DisplayName("Получение всех вещей владельца")
    void getAllTest() {

        List<Item> itemList = new ArrayList<>();
        itemList.add(itemInRepository);
        itemList.add(itemInRepository2);

        //Коментарии в репозитории
        Comment commentInRepository1 = Comment.builder()
                .id(1L)
                .text("text")
                .author(userInRepository)
                .item(itemInRepository)
                .created(LocalDateTime.now())
                .build();

        Comment commentInRepository2 = Comment.builder()
                .id(2L)
                .text("text2")
                .author(userInRepository)
                .item(itemInRepository2)
                .created(LocalDateTime.now())
                .build();

        List<Comment> commentList = new ArrayList<>();
        commentList.add(commentInRepository1);
        commentList.add(commentInRepository2);

        //Бронирования в репозитории
        Booking bookingInRepository1 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().plusWeeks(1L))
                .end(LocalDateTime.now().plusWeeks(2L))
                .item(itemInRepository)
                .booker(userInRepository)
                .status(APPROVED)
                .build();

        Booking bookingInRepository2 = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().minusWeeks(2L))
                .end(LocalDateTime.now().minusWeeks(1L))
                .item(itemInRepository)
                .booker(userInRepository)
                .status(APPROVED)
                .build();

        List<Booking> bookingList = new ArrayList<>();
        bookingList.add(bookingInRepository1);
        bookingList.add(bookingInRepository2);

        //Ожидаемые данные
        ItemDto itemDtoOutput1 = ItemDto.builder()
                .id(1L)
                .name("Дрель")
                .description("Простая дрель")
                .available(true)
                .comments(Collections.singletonList(CommentMapper.toDto(commentInRepository1)))
                .lastBooking(BookingMapper.toShortDto(bookingInRepository1))
                .nextBooking(BookingMapper.toShortDto(bookingInRepository2))
                .requestId(1L)
                .build();

        ItemDto itemDtoOutput2 = ItemDto.builder()
                .id(2L)
                .name("Дрель++")
                .description("Мощная дрель")
                .available(true)
                .comments(Collections.singletonList(CommentMapper.toDto(commentInRepository2)))
                .requestId(1L)
                .build();

        List<ItemDto> itemDtoListOutput = new ArrayList<>();
        itemDtoListOutput.add(itemDtoOutput1);
        itemDtoListOutput.add(itemDtoOutput2);

        when(mockUserRepository.existsById(anyLong()))
                .thenReturn(true);
        when(mockItemRepository.findAllByOwnerId(anyLong(), any(Pageable.class)))
                .thenReturn(itemList);
        when(mockBookingRepository.findAllByItemInAndStatus(anyList(), any(BookingStatus.class)))
                .thenReturn(bookingList);
        when(mockCommentRepository.findAllByItemIdIn(anyList(), any()))
                .thenReturn(commentList);

        assertEquals(itemDtoListOutput, itemService.getAll(1L, 0, 10));
    }

    @Test
    @DisplayName("Поиск вещи")
    void findTest() {

        List<Item> itemList = new ArrayList<>();
        itemList.add(itemInRepository);
        itemList.add(itemInRepository2);

        //Ожидаемый результат
        List<ItemDto> itemDtoLisOutput = itemList.stream().map(ItemMapper::toDto).collect(Collectors.toList());

        assertEquals(new ArrayList<>(), itemService.find("", 0, 10));

        when(mockItemRepository.findAllByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue(anyString(), anyString(), any(Pageable.class)))
                .thenReturn(itemList);

        assertEquals(itemDtoLisOutput, itemService.find("Дрель", 0, 10));
    }

    @Test
    @DisplayName("Удаление вещи")
    void deleteTest() {
        when(mockUserRepository.existsById(anyLong()))
                .thenReturn(true);
        when(mockItemRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemInRepository));

        assertDoesNotThrow(() -> itemService.delete(1L, 1L));
    }

    @Test
    @DisplayName("Сохранение комментария")
    void createCommentTest() {
        //Коммент в репозитории
        Comment commentInRepository = Comment.builder()
                .id(1L)
                .text("Быстро греется")
                .author(userInRepository)
                .item(itemInRepository)
                .build();

        //Бронирвание в репозитории
        Booking bookingInRepository = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().minusMonths(1))
                .end(LocalDateTime.now().minusWeeks(2))
                .item(itemInRepository)
                .booker(userInRepository)
                .status(APPROVED)
                .build();

        List<Booking> listOfBookings = new ArrayList<>(Collections.singletonList(bookingInRepository));

        //Входные даные
        CommentDto commentDtoInput = CommentMapper.toDto(commentInRepository);

        when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.of(userInRepository));
        when(mockItemRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemInRepository));

        assertThrows(IllegalArgumentException.class, () -> itemService.createComment(commentDtoInput, 1L, 1L));

        when(mockBookingRepository.findAllByBookerIdAndItemIdAndStatusAndEndBefore(anyLong(), anyLong(), any(BookingStatus.class), any(LocalDateTime.class)))
                .thenReturn(listOfBookings);

        CommentDto commentDtoOutput = itemService.createComment(commentDtoInput, 1L, 1L);

        assertEquals(commentDtoInput.getId(), commentDtoOutput.getId());
        assertEquals(commentDtoInput.getText(), commentDtoOutput.getText());
        assertEquals(commentDtoInput.getAuthorName(), commentDtoOutput.getAuthorName());
    }


    @Test
    @DisplayName("Проверка исключения NotFound")
    void NotFoundTest() {
        ItemDto itemDtoInput = ItemDto.builder()
                .name("update")
                .description("update")
                .available(false)
                .build();

        //Коммент в репозитории
        Comment commentInRepository = Comment.builder()
                .id(1L)
                .text("Быстро греется")
                .author(userInRepository)
                .item(itemInRepository)
                .build();
        //Проверка изменения
        assertThrows(NotFound.class, () -> itemService.update(itemDtoInput, 2L, 1L));
        //Проверка получения
        assertThrows(NotFound.class, () -> itemService.get(1L, 1L));
        assertThrows(NotFound.class, () -> itemService.getAll(1L, 0, 10));
        //Проверка удаления
        assertThrows(NotFound.class, () -> itemService.delete(1L, 1L));
        //Проверка создания комментария
        assertThrows(NotFound.class, () -> itemService.createComment(CommentMapper.toDto(commentInRepository), 1L, 1L));
        when(mockUserRepository.existsById(anyLong()))
                .thenReturn(true);
        assertThrows(NotFound.class, () -> itemService.get(1L, 1L));
        assertThrows(NotFound.class, () -> itemService.delete(1L, 2L));
        assertThrows(NotFound.class, () -> itemService.createComment(CommentMapper.toDto(commentInRepository), 1L, 1L));
    }
}
