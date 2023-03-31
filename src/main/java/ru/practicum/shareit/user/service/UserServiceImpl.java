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
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    @Override
    public UserDto create(UserDto userDto) {
        User user = UserMapper.toModel(userDto);
        return UserMapper.toDto(userDao.create(user));
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        User user = UserMapper.toModel(userDto);
        User userToUpdate = UserMapper.toModel(get(id));
        if (user.getName() != null) {
            userToUpdate.setName(user.getName());
        }
        if (user.getEmail() != null) {
            userDao.removeEmail(userToUpdate.getEmail());
            userToUpdate.setEmail(user.getEmail());
        }
        return UserMapper.toDto(userDao.update(userToUpdate));
    }

    @Override
    public UserDto get(Long id) {
        return UserMapper.toDto(userDao.get(id));
    }

    @Override
    public List<UserDto> get() {
        return userDao.get()
                .stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        userDao.delete(id);
    }
}
