package ru.practicum.shareit.user.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.EmailAlreadyInUse;
import ru.practicum.shareit.exception.NotFound;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class InMemoryUserDaoImpl implements UserDao {

    private final Map<Long, User> users = new HashMap<>();
    private final Map<String, Long> emails = new HashMap<>();
    private Long id = 1L;

    @Override
    public User create(User user) {
        user.setId(id);
        checkEmail(user);
        id++;
        emails.put(user.getEmail(), user.getId());
        users.put(user.getId(), user);
        log.debug("Пользователь создан {}", user);
        return user;
    }

    @Override
    public User update(User user) {
        checkEmail(user);
        emails.put(user.getEmail(), user.getId());
        users.put(user.getId(), user);
        log.debug("Пользователь обновлён {}", user);
        return user;
    }

    @Override
    public User get(Long id) {
        checkExistence(id);
        return users.get(id);
    }

    @Override
    public List<User> get() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void delete(Long id) {
        checkExistence(id);
        removeEmail(get(id).getEmail());
        users.remove(id);
    }

    @Override
    public void removeEmail(String email) {
        emails.remove(email);
    }

    /**
     * Метод проверки дублирования поля Email
     *
     * @throws EmailAlreadyInUse Адрес электронной почты уже используется
     */
    private void checkEmail(User user) {
        if (emails.containsKey(user.getEmail()) && !emails.get(user.getEmail()).equals(user.getId())) {
            log.debug("Дубликат электронного адреса {}", user.getEmail());
            throw new EmailAlreadyInUse();
        }
    }

    /**
     * Метод проверки сущетвования пользователя
     *
     * @throws NotFound Пользователь не найден
     */
    private void checkExistence(Long id) {
        if (!users.containsKey(id)) {
            log.debug("Пользователь не найден. ID = {}", id);
            throw new NotFound("пользователь", id);
        }
    }
}
