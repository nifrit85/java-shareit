package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

public interface UserDao {
    User create(User user);

    User update(Long id, User user);

    User get(Long id);

    void delete(Long id);

}
