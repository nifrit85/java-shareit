package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingDto;
import ru.practicum.shareit.booking.model.BookingRequestDto;
import ru.practicum.shareit.exception.NotAvailable;
import ru.practicum.shareit.exception.NotFound;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingService {

    /**
     * Метод добавления нового бронирования
     *
     * @param bookingDto Объект DTO бронирования
     * @param userId     Id владельца вещи
     * @return Объект DTO бронирования
     * @throws NotFound     Пользователь не найден
     * @throws NotFound     Вещь не найдена
     * @throws NotAvailable Вещь не доступна для брониварония
     */
    BookingDto create(BookingRequestDto bookingDto, Long userId);

    /**
     * Метод подтверждения бронирования
     *
     * @param bookingId Id бронирования
     * @param userId    Id владельца вещи
     * @param approved  Подтверждение\отклонение бронирования
     * @return Объект DTO бронирования
     * @throws NotFound                 Пользователь не найден
     * @throws NotFound                 Бронивароние не найдено
     * @throws IllegalArgumentException Бронирование уже обработано
     */
    BookingDto approve(Long bookingId, Long userId, Boolean approved);

    /**
     * Метод получения брониварония бронирования
     *
     * @param bookingId Id бронирования
     * @param userId    Id владельца вещи или того, кто забронировал
     * @return Объект DTO бронирования
     * @throws NotFound Пользователь не найден
     * @throws NotFound Бронивароние не найдено
     */
    BookingDto get(Long bookingId, Long userId);

    /**
     * Метод получения списка бронирования
     *
     * @param state  Стутус бронирования
     * @param userId Id пользователя
     * @return Список объектов DTO бронирования
     * @throws NotFound                 Пользователь не найден
     * @throws IllegalArgumentException Неверный статус
     */
    List<BookingDto> get(String state, Long userId);

    /**
     * Метод получения списка бронирования для владельца вещей
     *
     * @param state  Стутус бронирования
     * @param userId Id владельца вещи
     * @return Список объектов DTO бронирования
     * @throws NotFound                 Пользователь не найден
     * @throws IllegalArgumentException Неверный статус
     */
    List<BookingDto> getByOwner(String state, Long userId);

    /**
     * Метод получения списка бронирования
     *
     * @param items  Список вещей
     * @param status Статус бронирования
     * @return Список объектов бронирования
     */

    List<Booking> findAllByItemInAndStatus(List<Item> items, BookingStatus status);

    /**
     * Метод проверяет бралась ли вещь в аренду
     *
     * @param userId Id пользователя
     * @param itemId Id вещи
     * @param status Статус бронирования
     * @param end    Окончание срока бронирования
     * @return Список объектов бронирования
     */
    List<Booking> findAllByBookerIdAndItemIdAndStatusAndEndBefore(Long userId, Long itemId, BookingStatus status, LocalDateTime end);
}
