package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
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
    private static final String OWNER_ID = "X-Sharer-User-Id";
    private final ItemService itemService;

    @ResponseStatus(HttpStatus.CREATED)
    @Validated({Marker.OnCreate.class})
    @PostMapping
    public ItemDto create(@RequestHeader(OWNER_ID) Long ownerId,
                          @Valid
                          @RequestBody ItemDto itemDto) {
        log.info("Запрос POST: create(ItemDto itemDto, Long ownerId) на создание вещи.");
        return itemService.create(itemDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(OWNER_ID) Long ownerId,
                          @RequestBody ItemDto itemDto,
                          @PathVariable Long itemId) {
        log.info("Запрос PATCH: update(ItemDto itemDto, Long ownerId) на обновление вещи с ID = {}.", itemId);
        return itemService.update(itemDto, itemId, ownerId);
    }

    @GetMapping("/{itemId}")
    public ItemDto get(@PathVariable Long itemId) {
        log.info("Запрос GET: get(itemId,ownerId) на получение вещи с ID = {}.", itemId);
        return itemService.get(itemId);
    }

    @GetMapping
    public List<ItemDto> getAll(@RequestHeader(OWNER_ID) Long ownerId) {
        log.info("Запрос GET: get(ownerId) на получение всех вещей.");
        return itemService.getAll(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> find(@RequestParam String text) {
        log.info("Запрос GET: find(text) на получение всех вещей содержащих '{}' в названии или описании.", text);
        return itemService.find(text);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@RequestHeader(OWNER_ID) Long ownerId,
                       @PathVariable Long itemId) {
        log.info("Запрос DELETE: delete(Long userId) на удаление вещи по ID = {}.", itemId);
        itemService.delete(itemId, ownerId);
    }
}
