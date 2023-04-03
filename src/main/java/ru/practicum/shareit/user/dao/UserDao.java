package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserDao {
    User create(User user);

    User update(Long id, User user);

    User get(Long id);

    List<User> get();

    void delete(Long id);

}
