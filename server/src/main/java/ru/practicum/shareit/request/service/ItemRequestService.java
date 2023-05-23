package ru.practicum.shareit.request.service;

import ru.practicum.shareit.exception.NotFound;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    /**
     * Метод создания запроса вещи
     *
     * @param itemRequestDto Объект DTO запроса
     * @param userId         Id пользователя
     * @return Объект DTO запроса
     * @throws NotFound пользователь не найден
     */
    ItemRequestDto create(ItemRequestDto itemRequestDto, Long userId);

    /**
     * Метод получает список своих запросов
     *
     * @param userId Id владельца запросов
     * @return Список DTO объектов запроса
     * @throws NotFound пользователь не найден
     */
    List<ItemRequestDto> get(Long userId);

    /**
     * Метод получения списка запросов, созданных другими пользователями, постранично
     *
     * @param userId Id владельца запросов
     * @param from   Индекс первого элемента
     * @param size   Количество элементов для отображения
     * @return Список DTO объектов запроса
     * @throws NotFound пользователь не найден
     */
    List<ItemRequestDto> get(Long userId, Integer from, Integer size);

    /**
     * Метод получения данных об одном конкретном запросе.
     *
     * @param userId    Id пользователя
     * @param requestId Id запроса
     * @return DTO объект запроса
     * @throws NotFound пользователь не найден
     * @throws NotFound запрос не найден
     */
    ItemRequestDto get(Long userId, Long requestId);

    /**
     * Метод получения данных об одном конкретном запросе.
     *
     * @param requestId Id запроса
     * @return объект запроса
     */
    ItemRequest getRequest(Long requestId);
}
