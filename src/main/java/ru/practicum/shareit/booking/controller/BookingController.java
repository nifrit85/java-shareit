package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.booking.model.BookingRequestDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping(path = "/bookings")
@Slf4j
@Validated
@RequiredArgsConstructor
public class BookingController {
    private static final String USER_ID = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public BookingDto create(@RequestBody @Valid BookingRequestDto bookingDto,
                             @RequestHeader(USER_ID) Long userId) {
        log.info("Запрос POST: create(BookingInDto bookingDto, Long userId) на создание бронирования.");
        return bookingService.create(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(@PathVariable Long bookingId,
                              @RequestParam Boolean approved,
                              @RequestHeader(USER_ID) Long userId) {
        log.info("Запрос PATCH: approve(Long bookingId, Long userId, Boolean approved) на подтверждение или отклонение запроса на бронирование с ID = {}.", bookingId);
        return bookingService.approve(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto get(@PathVariable Long bookingId,
                          @RequestHeader(USER_ID) Long userId) {
        log.info("Запрос GET: get(Long bookingId,Long userId) на получение бронирования с ID = {}.", bookingId);
        return bookingService.get(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> get(@RequestParam(defaultValue = "ALL") String state,
                                @RequestHeader(USER_ID) Long userId) {
        log.info("Запрос GET: get(Long bookingId,Long userId) на получение списка всех бронирований текущего пользователя с ID = {}.", userId);
        return bookingService.get(state, userId);
    }

    @GetMapping("/owner")
    public List<BookingDto> getByOwner(@RequestParam(defaultValue = "ALL") String state,
                                       @RequestHeader(USER_ID) Long userId) {
        log.info("Запрос GET: getByOwner(Long bookingId,Long userId) на получение списка бронирований для всех вещей текущего пользователя с ID = {}.", userId);
        return bookingService.getByOwner(state, userId);
    }

}
