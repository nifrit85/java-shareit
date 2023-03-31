package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemDao itemDao;
    private final UserService userService;

    @Override
    public ItemDto create(ItemDto itemDto, Long ownerId) {
        Item item = ItemMapper.toModel(itemDto);
        item.setOwner(UserMapper.toModel(userService.get(ownerId)));
        return ItemMapper.toDto(itemDao.create(item));
    }

    @Override
    public ItemDto update(ItemDto itemDto, Long itemId, Long ownerId) {
        Item item = ItemMapper.toModel(itemDto);
        item.setId(itemId);
        item.setOwner(UserMapper.toModel(userService.get(ownerId)));
        return ItemMapper.toDto(itemDao.update(item));
    }

    @Override
    public ItemDto get(Long itemId) {
        return ItemMapper.toDto(itemDao.get(itemId));
    }

    @Override
    public List<ItemDto> getAll(Long ownerId) {
        return itemDao.getAll(ownerId)
                .stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> find(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemDao.find(text.toLowerCase())
                .stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long itemId, Long ownerId) {
        itemDao.delete(itemId, ownerId);
    }
}
