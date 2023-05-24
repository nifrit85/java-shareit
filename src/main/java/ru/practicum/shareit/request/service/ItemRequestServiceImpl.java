package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFound;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemRequestDto;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor_ = {@Lazy})
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    @Transactional
    public ItemRequestDto create(ItemRequestDto itemRequestDto, Long userId) {
        User user = UserMapper.toModel(userService.get(userId));
        ItemRequest itemRequest = ItemRequestMapper.toModel(itemRequestDto, LocalDateTime.now(), user);
        ItemRequest savedItemRequest = itemRequestRepository.save(itemRequest);
        log.debug("Добавлен запрос на вещь {}", savedItemRequest);
        return ItemRequestMapper.toDto(savedItemRequest, new ArrayList<>());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> get(Long userId) {
        userService.existsById(userId);
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorIdOrderByCreatedAsc(userId);

        return setItemsToItemRequestAndTransformToDto(itemRequests);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> get(Long userId, Integer from, Integer size) {
        userService.existsById(userId);
        Pageable pageable = PageRequest.of(
                from == 0 ? 0 : (from / size),
                size,
                Sort.by(Sort.Direction.ASC, "created"));
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorIdNot(userId, pageable);
        return setItemsToItemRequestAndTransformToDto(itemRequests);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestDto get(Long userId, Long requestId) {
        userService.existsById(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId).orElseThrow(() -> new NotFound("запрос", requestId));
        List<ItemRequestDto> itemRequestDtos = setItemsToItemRequestAndTransformToDto(Collections.singletonList(itemRequest));
        if (itemRequestDtos.isEmpty()) {
            return null;
        } else {
            return itemRequestDtos.get(0);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequest getRequest(Long requestId) {
        return itemRequestRepository.findById(requestId).orElse(null);
    }

    /**
     * Метод добавляет список вещей по запросу и преобразует запрос в DTO
     *
     * @param itemRequests список запросов
     * @return список запросов DTO
     */

    private List<ItemRequestDto> setItemsToItemRequestAndTransformToDto(List<ItemRequest> itemRequests) {
        Map<ItemRequest, List<Item>> allItems = itemService.findAllByRequestIn(itemRequests)
                .stream()
                .collect(Collectors.groupingBy(Item::getRequest, Collectors.toList()));

        List<ItemRequestDto> itemRequestDtos = new ArrayList<>();

        for (ItemRequest itemRequest : itemRequests) {
            List<Item> itemList = allItems.get(itemRequest);
            if (itemList == null) {
                itemList = new ArrayList<>();
            }

            ItemRequestDto itemRequestDto = ItemRequestMapper.toDto(itemRequest, itemList
                    .stream()
                    .map(ItemMapper::toItemShortDto)
                    .collect(Collectors.toList()));

            itemRequestDtos.add(itemRequestDto);

        }

        return itemRequestDtos;

    }
}
