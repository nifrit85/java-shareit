package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.model.CommentDto;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.marker.Marker;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private static final String USER_ID = "X-Sharer-User-Id";
    private final ItemService itemService;

    @ResponseStatus(HttpStatus.CREATED)
    @Validated({Marker.OnCreate.class})
    @PostMapping
    public ItemDto create(@RequestHeader(USER_ID) Long userId,
                          @Valid
                          @RequestBody ItemDto itemDto) {
        log.info("Запрос POST: create(ItemDto itemDto, Long userId) на создание вещи.");
        return itemService.create(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(USER_ID) Long userId,
                          @RequestBody ItemDto itemDto,
                          @PathVariable Long itemId) {
        log.info("Запрос PATCH: update(ItemDto itemDto, Long userId) на обновление вещи с ID = {}.", itemId);
        return itemService.update(itemDto, itemId, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto get(@PathVariable Long itemId,
                       @RequestHeader(USER_ID) Long userId) {
        log.info("Запрос GET: get(Long itemId, Long userId) на получение вещи с ID = {}.", itemId);
        return itemService.get(itemId, userId);
    }

    @GetMapping
    public List<ItemDto> getAll(@RequestHeader(USER_ID) Long userId) {
        log.info("Запрос GET: get(Long userId) на получение всех вещей.");
        return itemService.getAll(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> find(@RequestParam String text) {
        log.info("Запрос GET: find(String text) на получение всех вещей содержащих '{}' в названии или описании.", text);
        return itemService.find(text);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@RequestHeader(USER_ID) Long userId,
                       @PathVariable Long itemId) {
        log.info("Запрос DELETE: delete(Long userId) на удаление вещи по ID = {}.", itemId);
        itemService.delete(itemId, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader(USER_ID) Long userId,
                                    @Valid @RequestBody CommentDto commentDto,
                                    @PathVariable Long itemId) {
        log.info("Запрос POST: createComment(CommentDto commentDto, Long itemId, Long userId) на добавление комментария к вещи по ID = {}.", itemId);
        return itemService.createComment(commentDto, itemId, userId);

    }
}
