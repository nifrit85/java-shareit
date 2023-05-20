package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.marker.Marker;
import ru.practicum.shareit.request.client.ItemRequestClient;
import ru.practicum.shareit.request.model.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/requests")
@Slf4j
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private static final String USER_ID = "X-Sharer-User-Id";
    private final ItemRequestClient itemRequestClient;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    @Validated({Marker.OnCreate.class})
    public ResponseEntity<Object> create(@RequestHeader(USER_ID) Long userId,
                                         @Valid
                                         @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestClient.create(itemRequestDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> get(@RequestHeader(USER_ID) Long userId) {
        return itemRequestClient.get(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> get(@RequestHeader(USER_ID) Long userId,
                                      @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
                                      @RequestParam(defaultValue = "10") @Positive Integer size) {

        return itemRequestClient.get(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> get(@RequestHeader(USER_ID) Long userId,
                                      @PathVariable Long requestId) {

        return itemRequestClient.get(userId, requestId);
    }
}
