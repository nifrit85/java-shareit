package ru.practicum.shareit.item.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFound;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class InMemoryItemDaoImpl implements ItemDao {

    private final Map<Long, Item> items = new HashMap<>();
    private Long id = 1L;

    @Override
    public Item create(Item item) {
        item.setId(id++);
        items.put(item.getId(), item);
        log.debug("Вещь добавлена {}", item);
        return item;
    }

    @Override
    public Item update(Item item) {

        Item itemToUpdate = get(item.getId());
        if (notOwner(itemToUpdate, item.getOwner().getId())) {
            throw new NotFound("вещь", item.getId());
        }

        if (item.getName() != null) {
            itemToUpdate.setName(item.getName());
        }
        if (item.getDescription() != null) {
            itemToUpdate.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemToUpdate.setAvailable(item.getAvailable());
        }
        items.put(itemToUpdate.getId(), itemToUpdate);
        log.debug("Вещь обновлена {}", itemToUpdate);
        return itemToUpdate;
    }

    @Override
    public Item get(Long itemId) {
        return items.get(itemId);
    }

    @Override
    public List<Item> getAll(Long ownerId) {
        return items.values()
                .stream().filter(item -> item.getOwner().getId().equals(ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> find(String text) {
        return items.values()
                .stream().filter(Item::getAvailable)
                .filter(item -> item.getName().toLowerCase().contains(text)
                        || item.getDescription().toLowerCase().contains(text))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long itemId, Long ownerId) {
        Item item = get(itemId);
        if (notOwner(item, item.getOwner().getId())) {
            throw new NotFound("вещь", item.getId());
        }
        items.remove(item.getId());
    }

    private boolean notOwner(Item item, Long ownerId) {
        return item == null || !item.getOwner().getId().equals(ownerId);
    }
}
