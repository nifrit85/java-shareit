package ru.practicum.shareit.booking.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.model.BookingRequestDto;
import ru.practicum.shareit.client.BaseClient;

import java.util.Map;

@Service
@Slf4j
public class BookingClient extends BaseClient {
    private static final String API_PREFIX = "/bookings";
    private static final String UNSUPPORTED_STATUS = "Unknown state: UNSUPPORTED_STATUS";

    @Autowired
    public BookingClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> create(BookingRequestDto bookingDto, Long userId) {
        log.info("Запрос POST: create(BookingInDto bookingDto, Long userId) на создание бронирования.");
        checkBeforeCreate(bookingDto);
        return post("", userId, bookingDto);
    }

    public ResponseEntity<Object> approve(Long bookingId, Long userId, Boolean approved) {
        log.info("Запрос PATCH: approve(Long bookingId, Long userId, Boolean approved) на подтверждение или отклонение запроса на бронирование с ID = {}.", bookingId);
        Map<String, Object> parameters = Map.of("approved", approved);
        return patch("/" + bookingId + "?approved={approved}", userId, parameters, null);
    }

    public ResponseEntity<Object> get(Long bookingId, Long userId) {
        log.info("Запрос GET: get(Long bookingId,Long userId) на получение бронирования с ID = {}.", bookingId);
        return get("/" + bookingId, userId);
    }

    public ResponseEntity<Object> get(String state, Long userId, Integer from, Integer size) {
        log.info("Запрос GET: get(String state,Long userId, Integer from, Integer size) на получение списка всех бронирований, постранично, текущего пользователя с ID = {}.", userId);
        checkValidState(state);
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size,
                "state", state);
        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getByOwner(String state, Long userId, Integer from, Integer size) {
        log.info("Запрос GET: getByOwner(String state,Long userId, Integer from, Integer size) на получение списка бронирований, постранично, для всех вещей текущего пользователя с ID = {}.", userId);
        checkValidState(state);
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size,
                "state", state);
        return get("/owner?state={state}&from={from}&size={size}", userId, parameters);
    }

    /**
     * Проверки бронивароная перед созданием
     *
     * @param bookingDto Объект DTO ронирования
     * @throws IllegalArgumentException Неверные даты
     */
    private void checkBeforeCreate(BookingRequestDto bookingDto) {
        if (bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            throw new IllegalArgumentException("Дата окончания бронирования не может быть раньше даты начала бронирования");
        }
        if (bookingDto.getStart().isEqual(bookingDto.getEnd())) {
            throw new IllegalArgumentException("Дата окончания бронирования не может быть равна дате начала бронирования");
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