package ru.practicum.shareit.exception;

public class NotFound extends RuntimeException {
    public NotFound(String type, Long id) {
        super("Не найден " + type + " c ID = " + id);
    }
}
