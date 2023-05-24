package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFound;
import ru.practicum.shareit.item.CommentMapper;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.CommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.ItemDto;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor()
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingService bookingService;
    private final CommentRepository commentRepository;
    private final ItemRequestService itemRequestService;

    private final Sort sortComments = Sort.by(Sort.Direction.DESC, "created");

    @Override
    @Transactional
    public ItemDto create(ItemDto itemDto, Long userId) {
        Item item = ItemMapper.toModel(itemDto);
        item.setOwner(UserMapper.toModel(userService.get(userId)));
        if (itemDto.getRequestId() != null) {
            item.setRequest(itemRequestService.getRequest(itemDto.getRequestId()));
        }
        Item savedItem = itemRepository.save(item);
        log.debug("Вещь добавлена {}", savedItem);
        return ItemMapper.toDto(savedItem);
    }

    @Override
    @Transactional
    public ItemDto update(ItemDto itemDto, Long itemId, Long userId) {
        userService.existsById(userId);

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
        userService.existsById(userId);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFound("вещь", itemId));
        ItemDto itemDto = ItemMapper.toDto(item);
        itemDto.setComments(commentRepository.findAllByItemIdIn(Collections.singletonList(itemDto.getId()), sortComments)
                .stream()
                .map(CommentMapper::toDto)
                .collect(Collectors.toList()));
        if (itemDto.getComments() == null) {
            itemDto.setComments(new ArrayList<>());
        }
        if (!item.getOwner().getId().equals(userId)) {
            return itemDto;
        }
        LocalDateTime now = LocalDateTime.now();
        List<Booking> allBooking = bookingService.findAllByItemInAndStatus(Collections.singletonList(item), BookingStatus.APPROVED);

        itemDto.setLastBooking(BookingMapper.toShortDto(allBooking.stream()
                .filter(booking -> (booking.getStart().isBefore(now))).max(Comparator.comparing(Booking::getStart)).orElse(null)));

        itemDto.setNextBooking(BookingMapper.toShortDto(allBooking.stream()
                .filter(booking -> (booking.getStart().isAfter(now))).min(Comparator.comparing(Booking::getStart)).orElse(null)));

        return itemDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getAll(Long userId, Integer from, Integer size) {
        userService.existsById(userId);
        Pageable pageable = PageRequest.of(
                from == 0 ? 0 : (from / size),
                size,
                Sort.by(Sort.Direction.ASC, "id"));

        List<Item> items = itemRepository.findAllByOwnerId(userId, pageable);

        Map<Item, List<Booking>> allBooking = bookingService.findAllByItemInAndStatus(items, BookingStatus.APPROVED)
                .stream()
                .collect(Collectors.groupingBy(Booking::getItem, Collectors.toList()));

        Map<Item, List<Comment>> allComments = commentRepository.findAllByItemIdIn(items.stream().map(Item::getId).collect(Collectors.toList()), sortComments)
                .stream()
                .collect(Collectors.groupingBy(Comment::getItem, Collectors.toList()));

        LocalDateTime now = LocalDateTime.now();

        List<ItemDto> dtoItems = new ArrayList<>();

        for (Item item : items) {

            ItemDto itemDto = ItemMapper.toDto(item);

            if (allBooking.get(item) != null) {
                itemDto.setLastBooking(BookingMapper.toShortDto(allBooking.get(item)
                        .stream()
                        .filter(booking -> (booking.getStart().isBefore(now)))
                        .max(Comparator.comparing(Booking::getStart))
                        .orElse(null)));

                itemDto.setNextBooking(BookingMapper.toShortDto(allBooking.get(item)
                        .stream()
                        .filter(booking -> (booking.getStart().isAfter(now)))
                        .min(Comparator.comparing(Booking::getStart))
                        .orElse(null)));
            }
            if (allComments.get(item) != null) {
                itemDto.setComments(allComments.get(item)
                        .stream()
                        .map(CommentMapper::toDto)
                        .collect(Collectors.toList()));
            }
            dtoItems.add(itemDto);

        }

        return dtoItems;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> find(String text, Integer from, Integer size) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        Pageable pageable = PageRequest.of(
                from == 0 ? 0 : (from / size),
                size);
        return itemRepository.findAllByNameOrDescriptionContainingIgnoreCaseAndAvailableTrue(text, text, pageable)
                .stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void delete(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFound("вещь", itemId));
        userService.existsById(userId);
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
        if (bookingService.findAllByBookerIdAndItemIdAndStatusAndEndBefore(userId, itemId, BookingStatus.APPROVED, LocalDateTime.now()).isEmpty()) {
            throw new IllegalArgumentException("Данная вещь не бралась Вами в аренду");
        }
        Comment comment = CommentMapper.toModel(commentDto, user, item);
        commentRepository.save(comment);
        return CommentMapper.toDto(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public Item get(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() -> new NotFound("вещь", itemId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Item> findAllByRequestIn(List<ItemRequest> itemRequests) {
        return itemRepository.findAllByRequestIn(itemRequests);
    }
}
