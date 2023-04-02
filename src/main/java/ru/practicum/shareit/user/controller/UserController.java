package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.model.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;


@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public UserDto create(@Valid
                          @RequestBody UserDto userDto) {
        log.info("Запрос POST: create(UserDto userDto) на создание пользователя.");
        return userService.create(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@RequestBody UserDto userDto,
                          @PathVariable Long userId) {
        log.info("Запрос PATCH: update(Long userId, UserDto userDto) на обновление пользователя с ID = {}.", userId);
        return userService.update(userId, userDto);
    }

    @GetMapping("/{userId}")
    public UserDto get(@PathVariable Long userId) {
        log.info("Запрос GET: get(Long userId) на получение пользователя по ID = {}.", userId);
        return userService.get(userId);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        log.info("Запрос DELETE: delete(Long userId) на удаление пользователя по ID = {}.", userId);
        userService.delete(userId);
    }


}
