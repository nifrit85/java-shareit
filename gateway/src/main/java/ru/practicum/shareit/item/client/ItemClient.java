package ru.practicum.shareit.item.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.model.CommentDto;
import ru.practicum.shareit.item.model.ItemDto;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> create(ItemDto itemDto, Long userId) {
        log.info("Запрос POST: create(ItemDto itemDto, Long userId) на создание вещи.");
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> update(ItemDto itemDto, Long itemId, Long userId) {
        log.info("Запрос PATCH: update(ItemDto itemDto, Long userId) на обновление вещи с ID = {}.", itemId);
        return patch("/" + itemId, userId, itemDto);
    }

    public ResponseEntity<Object> get(long itemId, long userId) {
        log.info("Запрос GET: get(Long itemId, Long userId) на получение вещи с ID = {}.", itemId);
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getAll(Long userId, Integer from, Integer size) {
        log.info("Запрос GET: getAll(Long userId,Integer from, Integer size) на получение всех вещей постранично.");
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size);
        return get("?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> find(String text, Integer from, Integer size) {
        log.info("Запрос GET: find(String text, Integer from, Integer size) на получение всех вещей, постранично, содержащих '{}' в названии или описании.", text);
        if (text.isBlank()) {
            return ResponseEntity.ok().body(List.of());
        }
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size);
        return get("/search?text={text}&size={size}&from={from}", null, parameters);
    }

    public void delete(Long itemId, Long userId) {
        log.info("Запрос DELETE: delete(Long itemId, Long userId) на удаление вещи по ID = {}.", itemId);
        delete("/" + itemId, userId);
    }

    public ResponseEntity<Object> createComment(CommentDto commentDto, Long itemId, Long userId) {
        log.info("Запрос POST: createComment(CommentDto commentDto, Long itemId, Long userId) на добавление комментария к вещи по ID = {}.", itemId);
        return post("/" + itemId + "/comment", userId, commentDto);
    }
}
