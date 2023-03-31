package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.UserDto;

import java.util.List;

public interface UserService {

    /**
     * Метод создания пользователя
     *
     * @param userDto объект DTO пользователя
     * @return Объект DTO пользователя
     */
    UserDto create(UserDto userDto);

    /**
     * Метод изменения пользователя
     *
     * @param id      Id пользователя
     * @param userDto объект DTO пользователя
     * @return Объект DTO пользователя
     */
    UserDto update(Long id, UserDto userDto);

    /**
     * Метод получения пользователя
     *
     * @param id Id пользователя
     * @return Объект DTO пользователя
     */
    UserDto get(Long id);

    /**
     * Метод получения списка всех пользователей
     *
     * @return Список DTO пользователей
     */
    List<UserDto> get();

    /**
     * Метод удаления пользователя
     *
     * @param id Id пользователя
     */
    void delete(Long id);
}
