package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {

    private final UserDao userDao;

    public UserDto create(UserDto userDto) {
        User user = UserMapper.toModel(userDto);
        return UserMapper.toDto(userDao.create(user));
    }

    public UserDto update(Long id, UserDto userDto) {
        User user = UserMapper.toModel(userDto);
        return UserMapper.toDto(userDao.update(id, user));
    }

    public UserDto get(Long id) {
        return UserMapper.toDto(userDao.get(id));
    }

    public List<UserDto> get() {
        return userDao.get()
                .stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    public void delete(Long id) {
        userDao.delete(id);
    }
}
