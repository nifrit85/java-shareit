package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserDao {
    /**
     * Метод создания пользователя
     *
     * @param user объект пользователя
     * @return Объект пользователя
     */
    User create(User user);

    /**
     * Метод изменения пользователя
     *
     * @param user объект пользователя
     * @return Объект пользователя
     */
    User update(User user);

    /**
     * Метод получения пользователя
     *
     * @param id Id пользователя
     * @return Объект пользователя
     */
    User get(Long id);

    /**
     * Метод получения списка всех пользователей
     *
     * @return Список пользователей
     */
    List<User> get();

    /**
     * Метод удаления пользователя
     *
     * @param id Id пользователя
     */
    void delete(Long id);

}
