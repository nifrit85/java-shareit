package ru.practicum.shareit.request.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.request.model.ItemRequestDto;

import java.util.Map;

@Service
@Slf4j
public class ItemRequestClient extends BaseClient {
    private static final String API_PREFIX = "/requests";

    @Autowired
    public ItemRequestClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> create(ItemRequestDto itemRequestDto, Long userId) {
        log.info("Запрос POST: create(ItemRequestDto itemRequestDto, Long userId) на создание запроса на добавление вещи.");
        return post("", userId, itemRequestDto);
    }

    public ResponseEntity<Object> get(Long userId) {
        log.info("Запрос GET: get(Long userId) на получение списка своих запросов.");
        return get("", userId);
    }

    public ResponseEntity<Object> get(Long userId, Integer from, Integer size) {
        log.info("Запрос GET: get(Long userId, Integer from, Integer size) получения списка запросов, созданных другими пользователями, постранично.");
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size);
        return get("/all?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> get(Long userId, Long requestId) {
        log.info("Запрос GET: get(Long userId, Long requestId) получение данных об одном конкретном запросе.");
        return get("/" + requestId, userId);
    }
}