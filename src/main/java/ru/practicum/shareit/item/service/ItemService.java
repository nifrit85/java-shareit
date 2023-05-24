package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.CommentDto;
import ru.practicum.shareit.item.model.ItemDto;

import java.util.List;

public interface ItemService {
    /**
     * Метод добавления новой вещи
     *
     * @param itemDto объект DTO вещи
     * @param userId  Id владельца вещи
     * @return Объект DTO вещи
     */
    ItemDto create(ItemDto itemDto, Long userId);

    /**
     * Метод изменения вещи
     *
     * @param itemDto объект DTO вещи
     * @param itemId  Id вещи, которую необходимо изменить
     * @param userId  Id владельца вещи
     * @return Объект DTO вещи
     */
    ItemDto update(ItemDto itemDto, Long itemId, Long userId);

    /**
     * Метод получения вещи
     *
     * @param itemId Id вещи
     * @param userId Id владельца вещи
     * @return Объект DTO вещи
     */
    ItemDto get(Long itemId, Long userId);

    /**
     * Метод получения всех вещей владльца
     *
     * @param userId Id владельца вещи
     * @return Список DTO вещей
     */
    List<ItemDto> getAll(Long userId);

    /**
     * Метод поиска вещи по наименованию или описанию
     *
     * @param text искомый текст
     * @return Список DTO вещей
     */
    List<ItemDto> find(String text);

    /**
     * Метод удаление вещи
     *
     * @param itemId Id вещи, которую необходимо удалить
     * @param userId Id владельца вещи
     */
    void delete(Long itemId, Long userId);

    /**
     * Метод добавления комментария
     *
     * @param commentDto Объект комментария
     * @param userId     Id пользователя, оставивщего комментарий
     * @param itemId     Id вещи к которой оставили комментарий
     * @return Объект комментария
     */
    CommentDto createComment(CommentDto commentDto, Long itemId, Long userId);
}
