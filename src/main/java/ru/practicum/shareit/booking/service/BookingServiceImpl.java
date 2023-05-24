package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.booking.model.BookingRequestDto;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotAvailable;
import ru.practicum.shareit.exception.NotFound;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private static final String BOOKING = "бронирование";
    private static final String UNSUPPORTED_STATUS = "Unknown state: UNSUPPORTED_STATUS";

    private final UserService userService;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final Sort sort = Sort.by(Sort.Direction.DESC, "start");

    @Override
    @Transactional
    public BookingDto create(BookingRequestDto bookingDto, Long userId) {

        User user = UserMapper.toModel(userService.get(userId));
        Item item = itemRepository.findById(bookingDto.getItemId()).orElseThrow(() -> new NotFound("вещь", bookingDto.getItemId()));
        checkBeforeCreate(bookingDto, user, item);
        Booking booking = bookingRepository.save(BookingMapper.toModel(bookingDto, item, user));
        log.debug("Бронирование создано {}", booking);
        return BookingMapper.toDto(booking);
    }

    @Override
    @Transactional
    public BookingDto approve(Long bookingId, Long userId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFound(BOOKING, bookingId));
        checkBeforeApprove(booking, userId);
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        log.debug("Бронирование обработано {}", booking);
        return BookingMapper.toDto(bookingRepository.save(booking));

    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto get(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new NotFound(BOOKING, bookingId));
        checkBeforeGet(booking, userId);
        return BookingMapper.toDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> get(String state, Long userId) {
        userService.get(userId);
        log.debug("Запрошен список бронирований пользователя с ID = {}, состояние {}", userId, state);
        checkValidState(state);
        switch (BookingState.valueOf(state)) {
            case ALL:
                return bookingRepository.findAllByBookerId(userId, sort)
                        .stream()
                        .map(BookingMapper::toDto)
                        .collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(), LocalDateTime.now(), sort)
                        .stream()
                        .map(BookingMapper::toDto)
                        .collect(Collectors.toList());
            case PAST:
                return bookingRepository.findAllByBookerIdAndEndIsBefore(userId, LocalDateTime.now(), sort)
                        .stream()
                        .map(BookingMapper::toDto)
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findAllByBookerIdAndStartIsAfter(userId, LocalDateTime.now(), sort)
                        .stream()
                        .map(BookingMapper::toDto)
                        .collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.WAITING, sort)
                        .stream()
                        .map(BookingMapper::toDto)
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.REJECTED, sort)
                        .stream()
                        .map(BookingMapper::toDto)
                        .collect(Collectors.toList());
            default:
                throw new IllegalArgumentException(UNSUPPORTED_STATUS);

        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getByOwner(String state, Long userId) {
        userService.get(userId);
        log.debug("Запрошен список бронирований вещей пользователя с ID = {}, состояние {}", userId, state);
        checkValidState(state);
        switch (BookingState.valueOf(state)) {
            case ALL:
                return bookingRepository.findAllByItemOwnerId(userId, sort)
                        .stream()
                        .map(BookingMapper::toDto)
                        .collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(), LocalDateTime.now(), sort)
                        .stream()
                        .map(BookingMapper::toDto)
                        .collect(Collectors.toList());
            case PAST:
                return bookingRepository.findAllByItemOwnerIdAndEndIsBefore(userId, LocalDateTime.now(), sort)
                        .stream()
                        .map(BookingMapper::toDto)
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findAllByItemOwnerIdAndStartIsAfter(userId, LocalDateTime.now(), sort)
                        .stream()
                        .map(BookingMapper::toDto)
                        .collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findAllByItemOwnerIdAndStatus(userId, BookingStatus.WAITING, sort)
                        .stream()
                        .map(BookingMapper::toDto)
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.findAllByItemOwnerIdAndStatus(userId, BookingStatus.REJECTED, sort)
                        .stream()
                        .map(BookingMapper::toDto)
                        .collect(Collectors.toList());
            default:
                throw new IllegalArgumentException(UNSUPPORTED_STATUS);
        }
    }

    private void checkBeforeCreate(BookingRequestDto bookingDto, User user, Item item) {
        if (bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            throw new IllegalArgumentException("Дата окончания бронирования не может быть раньше даты начала бронирования");
        }
        if (bookingDto.getStart().isEqual(bookingDto.getEnd())) {
            throw new IllegalArgumentException("Дата окончания бронирования не может быть равна дате начала бронирования");
        }
        if (user.getId().equals(item.getOwner().getId())) {
            throw new NotFound("вещь", item.getId());
        }
        if (!item.getAvailable()) {
            throw new NotAvailable(item.getId());
        }
    }

    private void checkBeforeApprove(Booking booking, Long userId) {
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFound(BOOKING, booking.getId());
        }
        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new IllegalArgumentException("Бронирование уже обработано");
        }
    }

    private void checkBeforeGet(Booking booking, Long userId) {
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFound(BOOKING, booking.getId());
        }
    }

    private void checkValidState(String state) {
        try {
            BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(UNSUPPORTED_STATUS);
        }
    }
}
