package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFound;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public ItemDto create(ItemDto itemDto, Long userId) {
        Item item = ItemMapper.toModel(itemDto);
        item.setOwner(UserMapper.toModel(userService.get(userId)));
        Item savedItem = itemRepository.save(item);
        log.debug("Вещь добавлена {}", savedItem);
        return ItemMapper.toDto(savedItem);
    }

    @Override
    @Transactional
    public ItemDto update(ItemDto itemDto, Long itemId, Long userId) {
        userService.get(userId);
        Item itemToUpdate = itemRepository.findById(itemId).orElseThrow(() -> new NotFound("вещь", itemId));

        if (!itemToUpdate.getOwner().getId().equals(userId)) {
            throw new NotFound("вещь", itemId);
        }
        if (itemDto.getName() != null) {
            itemToUpdate.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            itemToUpdate.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            itemToUpdate.setAvailable(itemDto.getAvailable());
        }
        Item updatedItem = itemRepository.save(itemToUpdate);
        log.debug("Вещь обновлена {}", updatedItem);

        return ItemMapper.toDto(updatedItem);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto get(Long itemId, Long userId) {
        userService.get(userId);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFound("вещь", itemId));
        ItemDto itemDto = ItemMapper.toDto(item);
        itemDto.setComments(commentRepository.findAllByItemId(itemDto.getId()).stream().map(CommentMapper::toDto).collect(Collectors.toList()));
        if (itemDto.getComments() == null) {
            itemDto.setComments(new ArrayList<>());
        }
        if (!item.getOwner().getId().equals(userId)) {
            return itemDto;
        }
        itemDto.setLastBooking(BookingMapper.toShortDto(bookingRepository.findTop1ByItemIdAndStartIsBeforeAndStatusOrderByStartDesc(itemDto.getId(), LocalDateTime.now(), BookingStatus.APPROVED)));
        itemDto.setNextBooking(BookingMapper.toShortDto(bookingRepository.findTop1ByItemIdAndStartIsAfterAndStatusOrderByStartAsc(itemDto.getId(), LocalDateTime.now(), BookingStatus.APPROVED)));

        return itemDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getAll(Long userId) {
        List<ItemDto> items = itemRepository.findAllByOwnerIdOrderByIdAsc(userId)
                .stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
        items.forEach(itemDto -> {
            itemDto.setLastBooking(BookingMapper.toShortDto(bookingRepository.findTop1ByItemIdAndStartIsBeforeAndStatusOrderByStartDesc(itemDto.getId(), LocalDateTime.now(), BookingStatus.APPROVED)));
            itemDto.setNextBooking(BookingMapper.toShortDto(bookingRepository.findTop1ByItemIdAndStartIsAfterAndStatusOrderByStartAsc(itemDto.getId(), LocalDateTime.now(), BookingStatus.APPROVED)));
            itemDto.setComments(commentRepository.findAllByItemId(itemDto.getId()).stream().map(CommentMapper::toDto).collect(Collectors.toList()));
            if (itemDto.getComments() == null) {
                itemDto.setComments(new ArrayList<>());
            }
        });

        return items;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> find(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.findAllByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue(text, text)
                .stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFound("вещь", itemId));
        if (!item.getOwner().getId().equals(userId)) {
            throw new NotFound("вещь", itemId);
        }
        itemRepository.deleteById(itemId);
    }

    @Override
    @Transactional
    public CommentDto createComment(CommentDto commentDto, Long itemId, Long userId) {
        User user = UserMapper.toModel(userService.get(userId));
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFound("вещь", itemId));
        if (bookingRepository.findAllByBookerIdAndItemIdAndStatusAndEndBefore(userId, itemId, BookingStatus.APPROVED, LocalDateTime.now()).isEmpty()) {
            throw new IllegalArgumentException("Данная вещь не бралась Вами в аренду");
        }
        Comment comment = CommentMapper.toModel(commentDto, user, item);
        commentRepository.save(comment);
        return CommentMapper.toDto(comment);
    }
}
