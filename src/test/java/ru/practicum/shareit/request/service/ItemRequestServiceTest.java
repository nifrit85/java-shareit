package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.NotFound;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemShortDto;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestDto;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@SpringBootTest
class ItemRequestServiceTest {
    @MockBean
    private ItemRequestRepository mockItemRequestRepository;
    @MockBean
    private UserRepository mockUserRepository;
    @MockBean
    private ItemRepository mockItemRepository;
    @Autowired
    private ItemRequestServiceImpl itemRequestService;
    private static final LocalDateTime SOME_DATE = LocalDateTime.of(2023, 5, 9, 11, 20, 0);
    private User userInRepository;
    private ItemRequest itemRequestInRepository;
    private ItemRequestDto itemRequestDtoInput;
    private ItemRequestDto itemRequestDtoOutput;
    private Item itemInRepository;

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
                .description("Хотел бы воспользоваться щёткой для обуви")
                .requestor(userInRepository)
                .created(SOME_DATE)
                .build();
        //Вещь в репозитории
        itemInRepository = Item.builder()
                .id(1L)
                .name("Белая щётка для обуви")
                .available(true)
                .owner(userInRepository)
                .request(itemRequestInRepository)
                .description("Щётка для обуви")
                .build();

        //Входные данные
        itemRequestDtoInput = ItemRequestDto.builder()
                .description("Хотел бы воспользоваться щёткой для обуви")
                .build();
        //Ожидаемые данные
        itemRequestDtoOutput = ItemRequestDto.builder()
                .id(1L)
                .description("Хотел бы воспользоваться щёткой для обуви")
                .created(SOME_DATE)
                .items(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("Сохранение реквеста")
    void createTest() {
        when(mockUserRepository.findById(anyLong()))
                .thenReturn(Optional.of(userInRepository));
        when(mockItemRequestRepository.save(any(ItemRequest.class)))
                .thenReturn(itemRequestInRepository);
        //Создание
        assertEquals(itemRequestDtoOutput, itemRequestService.create(itemRequestDtoInput, userInRepository.getId()));
    }

    @Test
    @DisplayName("Получение реквеста")
    void getTest() {
        //Входные данные
        ItemRequest itemRequestInRepository2 = ItemRequest.builder()
                .id(2L)
                .description("Хотел бы воспользоваться пылесосом")
                .requestor(userInRepository)
                .created(SOME_DATE)
                .build();
        List<ItemRequest> itemRequestListOutput = List.of(
                itemRequestInRepository,
                itemRequestInRepository2);

        Item item2 = Item.builder()
                .id(2L)
                .name("Синяя щётка для обуви")
                .available(true)
                .owner(userInRepository)
                .request(itemRequestInRepository)
                .description("Хорошая щётка")
                .build();
        List<Item> itemList = new ArrayList<>();
        itemList.add(itemInRepository);
        itemList.add(item2);

        List<ItemShortDto> itemShortDtoList = itemList.stream()
                .map(ItemMapper::toItemShortDto)
                .collect(Collectors.toList());

        //Ожидаемый результат
        List<ItemRequestDto> itemRequestDtoListOutput = List.of(
                ItemRequestMapper.toDto(itemRequestInRepository, itemShortDtoList),
                ItemRequestMapper.toDto(itemRequestInRepository2, new ArrayList<>()));

        when(mockUserRepository.existsById(anyLong()))
                .thenReturn(true);
        when(mockItemRepository.findAllByRequestIn(any()))
                .thenReturn(itemList);
        when(mockItemRequestRepository.findAllByRequestorIdOrderByCreatedAsc(anyLong()))
                .thenReturn(itemRequestListOutput);
        //Получение списка своих реквестов
        assertEquals(itemRequestDtoListOutput, itemRequestService.get(1L));
        //Получение списка реквестов созданными другими пользователями
        when(mockItemRequestRepository.findAllByRequestorIdNot(anyLong(), any(Pageable.class)))
                .thenReturn(itemRequestListOutput);
        assertEquals(itemRequestDtoListOutput, itemRequestService.get(1L, 0, 2));
        //Получение по ID
        when(mockItemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemRequestInRepository));
        assertEquals(itemRequestDtoListOutput.get(0),itemRequestService.get(1L, 1L));
        //Получение по ID без проверок
        assertEquals(itemRequestInRepository, itemRequestService.getRequest(1L));
    }


    @Test
    @DisplayName("Проверка исключения NotFound")
    void notFoundTest() {
        //Проверка создания
        assertThrows(NotFound.class, () -> itemRequestService.create(itemRequestDtoInput, 2L));
        //Проверка получения списка своих запросов
        assertThrows(NotFound.class, () -> itemRequestService.get(2L));
        //Проверка списка запросав других пользователей
        assertThrows(NotFound.class, () -> itemRequestService.get(2L, 0, 1));
        //Проверка получения конкретного запроса
        //Пользователь
        assertThrows(NotFound.class, () -> itemRequestService.get(2L, 2L));
        //Запрос
        when(mockUserRepository.existsById(anyLong()))
                .thenReturn(true);
        assertThrows(NotFound.class, () -> itemRequestService.get(2L, 2L));
        assertNull(itemRequestService.getRequest(2L));
    }
}
