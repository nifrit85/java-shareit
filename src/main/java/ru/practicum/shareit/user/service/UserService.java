package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;

import java.util.List;

public interface UserService {

    /**
     * Метод создания пользователя
     *
     * @param userDto объект DTO пользователя
     * @return Объект пользователя
     */
    User create(UserDto userDto);

    /**
     * Метод изменения пользователя
     *
     * @param id      Id пользователя
     * @param userDto объект DTO пользователя
     * @return Объект пользователя
     */
    User update(Long id, UserDto userDto);

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
