package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwnerIdOrderByIdAsc(Long userId);

    List<Item> findAllByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue(String name, String description);
}
