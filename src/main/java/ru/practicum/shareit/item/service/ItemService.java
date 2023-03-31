package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDto;

import java.util.List;

public interface ItemService {
    /**
     * Метод добавления новой вещи
     *
     * @param itemDto объект DTO вещи
     * @param ownerId Id владельца вещи
     * @return Объект вещи
     */
    Item create(ItemDto itemDto, Long ownerId);

    /**
     * Метод изменения вещи
     *
     * @param itemDto объект DTO вещи
     * @param itemId  Id вещи, которую необходимо изменить
     * @param ownerId Id владельца вещи
     * @return Объект вещи
     */
    Item update(ItemDto itemDto, Long itemId, Long ownerId);

    /**
     * Метод получения вещи
     *
     * @param itemId Id вещи
     * @return Объект вещи
     */
    Item get(Long itemId);

    /**
     * Метод получения всех вещей владльца
     *
     * @param ownerId Id владельца вещи
     * @return Список вещей
     */
    List<Item> getAll(Long ownerId);

    /**
     * Метод поиска вещи по наименованию или описанию
     *
     * @param text искомый текст
     * @return Список вещей
     */
    List<Item> find(String text);

    /**
     * Метод удаление вещи
     *
     * @param itemId  Id вещи, которую необходимо удалить
     * @param ownerId Id владельца вещи
     */
    void delete(Long itemId, Long ownerId);
}
