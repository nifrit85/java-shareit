package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.model.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {
    private static final String USER_ID = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ItemRequestDto create(@RequestHeader(USER_ID) Long userId,
                                 @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Запрос POST: create(ItemRequestDto ItemRequestDto, Long userId) на создание запроса на добавление вещи.");
        return itemRequestService.create(itemRequestDto, userId);
    }

    @GetMapping
    public List<ItemRequestDto> get(@RequestHeader(USER_ID) Long userId) {
        log.info("Запрос GET: get(Long userId) на получение списка своих запросов.");
        return itemRequestService.get(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> get(@RequestHeader(USER_ID) Long userId,
                                    @RequestParam(defaultValue = "0") Integer from,
                                    @RequestParam(defaultValue = "10") Integer size) {
        log.info("Запрос GET: get(Long userId, Integer from, Integer size) получения списка запросов, созданных другими пользователями, постранично.");
        return itemRequestService.get(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto get(@RequestHeader(USER_ID) Long userId,
                              @PathVariable Long requestId) {
        log.info("Запрос GET: get(Long userId, Long requestId) получение данных об одном конкретном запросе.");
        return itemRequestService.get(userId, requestId);
    }
}
