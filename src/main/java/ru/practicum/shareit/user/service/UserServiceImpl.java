package ru.practicum.shareit.user.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFound;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        User savedUser = userRepository.save(UserMapper.toModel(userDto));
        log.debug("Пользователь создан {}", savedUser);
        return UserMapper.toDto(savedUser);
    }

    @Override
    @Transactional
    public UserDto update(Long id, UserDto userDto) {
        User userToUpdate = userRepository.findById(id).orElseThrow(() -> new NotFound("пользователь", id));
        if (userDto.getName() != null) {
            userToUpdate.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            userToUpdate.setEmail(userDto.getEmail());
        }
        User updatedUser = userRepository.save(userToUpdate);
        log.debug("Пользователь обновлён {}", updatedUser);
        return UserMapper.toDto(updatedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto get(Long id) {
        return UserMapper.toDto(userRepository.findById(id).orElseThrow(() -> new NotFound("пользователь", id)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> get() {
        return userRepository.findAll()
                .stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}
