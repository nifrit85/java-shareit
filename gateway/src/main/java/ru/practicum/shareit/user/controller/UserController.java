package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.marker.Marker;
import ru.practicum.shareit.user.client.UserClient;
import ru.practicum.shareit.user.model.UserDto;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/users")
@Slf4j
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserClient userClient;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @Validated({Marker.OnCreate.class})
    public ResponseEntity<Object> create(@Valid
                                         @RequestBody UserDto userDto) {
        return userClient.create(userDto);
    }

    @Validated({Marker.OnUpdate.class})
    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@Valid
                                         @RequestBody UserDto userDto,
                                         @PathVariable Long userId) {
        return userClient.update(userId, userDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> get(@PathVariable Long userId) {
        return userClient.get(userId);
    }

    @GetMapping
    public ResponseEntity<Object> get() {
        return userClient.get();
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        userClient.delete(userId);
    }
}
