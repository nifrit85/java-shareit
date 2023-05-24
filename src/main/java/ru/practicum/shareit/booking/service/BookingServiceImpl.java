package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor_ = {@Lazy})
@Slf4j
public class BookingServiceImpl implements BookingService {

    private static final String BOOKING = "бронирование";
    private static final String UNSUPPORTED_STATUS = "Unknown state: UNSUPPORTED_STATUS";
    private final UserService userService;
    private final ItemService itemService;
    private final BookingRepository bookingRepository;


    @Override
    @Transactional
    public BookingDto create(BookingRequestDto bookingDto, Long userId) {
        User user = UserMapper.toModel(userService.get(userId));
        Item item = itemService.get((bookingDto.getItemId()));
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
    public List<BookingDto> get(String state, Long userId, Integer from, Integer size) {
        userService.existsById(userId);
        log.debug("Запрошен список бронирований пользователя с ID = {}, состояние {}", userId, state);
        checkValidState(state);
        Pageable pageable = PageRequest.of(
                from == 0 ? 0 : (from / size),
                size,
                Sort.by(Sort.Direction.DESC, "start"));
        switch (BookingState.valueOf(state)) {
            case ALL:
                return bookingRepository.findAllByBookerId(userId, pageable)
                        .stream()
                        .map(BookingMapper::toDto)
                        .collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findAllByBookerIdAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(), LocalDateTime.now(), pageable)
                        .stream()
                        .map(BookingMapper::toDto)
                        .collect(Collectors.toList());
            case PAST:
                return bookingRepository.findAllByBookerIdAndEndIsBefore(userId, LocalDateTime.now(), pageable)
                        .stream()
                        .map(BookingMapper::toDto)
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findAllByBookerIdAndStartIsAfter(userId, LocalDateTime.now(), pageable)
                        .stream()
                        .map(BookingMapper::toDto)
                        .collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.WAITING, pageable)
                        .stream()
                        .map(BookingMapper::toDto)
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.REJECTED, pageable)
                        .stream()
                        .map(BookingMapper::toDto)
                        .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getByOwner(String state, Long userId, Integer from, Integer size) {
        userService.existsById(userId);
        log.debug("Запрошен список бронирований вещей пользователя с ID = {}, состояние {}", userId, state);
        checkValidState(state);
        Pageable pageable = PageRequest.of(
                from == 0 ? 0 : (from / size),
                size,
                Sort.by(Sort.Direction.DESC, "start"));
        switch (BookingState.valueOf(state)) {
            case ALL:
                return bookingRepository.findAllByItemOwnerId(userId, pageable)
                        .stream()
                        .map(BookingMapper::toDto)
                        .collect(Collectors.toList());
            case CURRENT:
                return bookingRepository.findAllByItemOwnerIdAndStartIsBeforeAndEndIsAfter(userId, LocalDateTime.now(), LocalDateTime.now(), pageable)
                        .stream()
                        .map(BookingMapper::toDto)
                        .collect(Collectors.toList());
            case PAST:
                return bookingRepository.findAllByItemOwnerIdAndEndIsBefore(userId, LocalDateTime.now(), pageable)
                        .stream()
                        .map(BookingMapper::toDto)
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findAllByItemOwnerIdAndStartIsAfter(userId, LocalDateTime.now(), pageable)
                        .stream()
                        .map(BookingMapper::toDto)
                        .collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findAllByItemOwnerIdAndStatus(userId, BookingStatus.WAITING, pageable)
                        .stream()
                        .map(BookingMapper::toDto)
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.findAllByItemOwnerIdAndStatus(userId, BookingStatus.REJECTED, pageable)
                        .stream()
                        .map(BookingMapper::toDto)
                        .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> findAllByItemInAndStatus(List<Item> items, BookingStatus status) {
        return bookingRepository.findAllByItemInAndStatus(items, status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> findAllByBookerIdAndItemIdAndStatusAndEndBefore(Long userId, Long itemId, BookingStatus status, LocalDateTime end) {
        return bookingRepository.findAllByBookerIdAndItemIdAndStatusAndEndBefore(userId, itemId, status, end);
    }

    /**
     * Проверки бронивароная перед созданием
     *
     * @param bookingDto Объект DTO ронирования
     * @param user       Пользователь создающий бронивароние
     * @param item       Вещь для бронирования
     * @throws IllegalArgumentException Неверные даты
     * @throws NotFound                 Не найдена вещь
     * @throws NotAvailable             Вещь не доступна для брониварония
     */
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

    /**
     * Проверки перед подтверждением брониварония
     *
     * @param booking Бронивароние
     * @param userId  Id пользователя
     * @throws NotFound                 Пользователь не найден
     * @throws NotFound                 Бронивароние не найдено
     * @throws IllegalArgumentException Бронирование уже обработано
     */

    private void checkBeforeApprove(Booking booking, Long userId) {
        userService.existsById(userId);
        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFound(BOOKING, booking.getId());
        }
        if (!booking.getStatus().equals(BookingStatus.WAITING)) {
            throw new IllegalArgumentException("Бронирование уже обработано");
        }
    }

    /**
     * Проверки перед получением бронирования
     *
     * @param booking Бронивароние
     * @param userId  Id пользователя
     * @throws NotFound Пользователь не найден
     * @throws NotFound Бронивароние не найдено
     */

    private void checkBeforeGet(Booking booking, Long userId) {
        userService.existsById(userId);
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFound(BOOKING, booking.getId());
        }
    }

    /**
     * Проверка ввода корректного статуса
     *
     * @param state Статус
     * @throws IllegalArgumentException Не корректный статус
     */
    private void checkValidState(String state) {
        try {
            BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(UNSUPPORTED_STATUS);
        }
    }
}
