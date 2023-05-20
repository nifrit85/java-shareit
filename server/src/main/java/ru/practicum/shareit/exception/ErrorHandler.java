package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RestControllerAdvice(basePackages = "ru.practicum.shareit")
public class ErrorHandler {

    public static final String ERROR = "error";

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleDataIntegrityViolationException(final DataIntegrityViolationException e) {
        String message = NestedExceptionUtils.getMostSpecificCause(e).getMessage();
        log.warn(message);
        return Map.of(ERROR, message);
    }

    @ExceptionHandler({ConstraintViolationException.class,
            MissingRequestHeaderException.class,
            IllegalArgumentException.class,
            NotAvailable.class,
            MissingServletRequestParameterException.class,
            MethodArgumentNotValidException.class,
            UnsupportedStatus.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleException(final Exception e) {
        log.warn(e.getMessage());
        return Map.of(ERROR, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFound(final NotFound e) {
        log.warn(e.getMessage());
        return Map.of(ERROR, e.getMessage());
    }
}
