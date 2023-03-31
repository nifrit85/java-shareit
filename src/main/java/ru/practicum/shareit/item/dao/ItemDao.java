package ru.practicum.shareit.item.dao;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemDao {
    /**
     * Метод добавления новой вещи
     *
     * @param item Объект вещи
     * @return Объект вещи
     */
    Item create(Item item);

    /**
     * Метод изменения вещи
     *
     * @param item Объект вещи
     * @return Объект вещи
     */
    Item update(Item item);

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
