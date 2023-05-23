package ru.practicum.shareit.exception;

public class NotAvailable extends RuntimeException {
    public NotAvailable(Long id) {
        super("Вещь с ID = " + id + " не доступна для бронирования");
    }
}
