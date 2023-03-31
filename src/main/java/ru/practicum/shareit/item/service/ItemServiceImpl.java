package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dao.ItemDao;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemDao itemDao;
    private final UserService userService;

    @Override
    public Item create(ItemDto itemDto, Long ownerId) {
        Item item = ItemMapper.toModel(itemDto);
        item.setOwner(userService.get(ownerId));
        return itemDao.create(item);
    }

    @Override
    public Item update(ItemDto itemDto, Long itemId, Long ownerId) {
        Item item = ItemMapper.toModel(itemDto);
        item.setId(itemId);
        item.setOwner(userService.get(ownerId));
        return itemDao.update(item);
    }

    @Override
    public Item get(Long itemId) {
        return itemDao.get(itemId);
    }

    @Override
    public List<Item> getAll(Long ownerId) {
        return itemDao.getAll(ownerId);
    }

    @Override
    public List<Item> find(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemDao.find(text.toLowerCase());
    }

    @Override
    public void delete(Long itemId, Long ownerId) {
        itemDao.delete(itemId, ownerId);
    }
}
