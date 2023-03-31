package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;

import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    @Override
    public User create(UserDto userDto) {
        User user = UserMapper.toModel(userDto);
        return userDao.create(user);
    }

    @Override
    public User update(Long id, UserDto userDto) {
        User user = UserMapper.toModel(userDto);
        user.setId(id);
        return userDao.update(user);
    }

    @Override
    public User get(Long id) {
        return userDao.get(id);
    }

    @Override
    public List<User> get() {
        return userDao.get();
    }

    @Override
    public void delete(Long id) {
        userDao.delete(id);
    }
}
