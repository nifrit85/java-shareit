package ru.practicum.shareit.user.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.EmailAlreadyInUse;
import ru.practicum.shareit.exception.NotFound;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
@Slf4j
public class InMemoryUserDaoImpl implements UserDao {

    private final Map<Long, User> users = new HashMap<>();
    private Long id = 1L;

    @Override
    public User create(User user) {
        user.setId(id);
        checkEmail(user);
        id++;
        users.put(user.getId(), user);
        log.debug("Пользователь создан {}", user);
        return user;
    }

    @Override
    public User update(User user) {
        checkExistence(user.getId());
        checkEmail(user);
        User userToUpdate = get(user.getId());
        if (user.getName() != null) {
            userToUpdate.setName(user.getName());
        }
        if (user.getEmail() != null) {
            userToUpdate.setEmail(user.getEmail());
        }
        users.put(user.getId(), userToUpdate);
        log.debug("Пользователь обновлён {}", userToUpdate);
        return userToUpdate;
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
        users.remove(id);
    }

    /**
     * Метод проверки дублирования поля Email
     *
     * @throws EmailAlreadyInUse Адрес электронной почты уже используется
     */
    private void checkEmail(User user) {
        for (User userToCheck : users.values()) {
            if (userToCheck.getEmail().equals(user.getEmail()) && !Objects.equals(userToCheck.getId(), user.getId())) {
                log.debug("Дубликат электронного адреса {}", user.getEmail());
                throw new EmailAlreadyInUse();
            }
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
