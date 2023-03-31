package ru.practicum.shareit.exception;

public class EmailAlreadyInUse extends RuntimeException {
    public EmailAlreadyInUse() {
        super("Электронный адрес уже используется");
    }
}
