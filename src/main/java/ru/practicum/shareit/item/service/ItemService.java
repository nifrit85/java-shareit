package ru.practicum.shareit.item.service;

import ru.practicum.shareit.exception.NotFound;
import ru.practicum.shareit.item.model.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDto;

import java.util.List;

public interface ItemService {
    /**
     * Метод добавления новой вещи
     *
     * @param itemDto Объект DTO вещи
     * @param userId  Id владельца вещи
     * @return Объект DTO вещи
     * @throws NotFound Пользователь не найден
     */
    ItemDto create(ItemDto itemDto, Long userId);

    /**
     * Метод изменения вещи
     *
     * @param itemDto Объект DTO вещи
     * @param itemId  Id вещи, которую необходимо изменить
     * @param userId  Id владельца вещи
     * @return Объект DTO вещи
     * @throws NotFound Пользователь не найден
     * @throws NotFound Вещь не найдена
     */
    ItemDto update(ItemDto itemDto, Long itemId, Long userId);

    /**
     * Метод получения вещи
     *
     * @param itemId Id вещи
     * @param userId Id владельца вещи
     * @return Объект DTO вещи
     * @throws NotFound Пользователь не найден
     * @throws NotFound Вещь не найдена
     */
    ItemDto get(Long itemId, Long userId);

    /**
     * Метод получения всех вещей владльца
     *
     * @param userId Id владельца вещи
     * @return Список DTO вещей
     * @throws NotFound Пользователь не найден
     */
    List<ItemDto> getAll(Long userId);

    /**
     * Метод поиска вещи по наименованию или описанию
     *
     * @param text Искомый текст
     * @return Список DTO вещей
     */
    List<ItemDto> find(String text);

    /**
     * Метод удаление вещи
     *
     * @param itemId Id вещи, которую необходимо удалить
     * @param userId Id владельца вещи
     * @throws NotFound Пользователь не найден
     * @throws NotFound Вещь не найдена
     */
    void delete(Long itemId, Long userId);

    /**
     * Метод добавления комментария
     *
     * @param commentDto Объект комментария
     * @param userId     Id пользователя, оставивщего комментарий
     * @param itemId     Id вещи к которой оставили комментарий
     * @return Объект комментария
     * @throws NotFound Пользователь не найден
     * @throws NotFound Вещь не найдена
     */
    CommentDto createComment(CommentDto commentDto, Long itemId, Long userId);

    /**
     * Метод получения вещи без проверок
     *
     * @param itemId Id вещи
     * @return Объект вещи
     * @throws NotFound Вещь не найдена
     */
    Item get(Long itemId);
}
