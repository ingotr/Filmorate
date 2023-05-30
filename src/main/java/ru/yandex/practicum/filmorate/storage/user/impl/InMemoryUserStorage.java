package ru.yandex.practicum.filmorate.storage.user.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validation.UserValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private final UserValidator userValidator;
    private int idCounter = 1;

    @Autowired
    public InMemoryUserStorage(UserValidator userValidator) {
        this.userValidator = userValidator;
    }

    @Override
    public User add(User user) {
        boolean valid = userValidator.validate(user);
        if (valid) {
            user.setId(getIdCounter());
            users.put(user.getId(), user);
            log.debug("- getResponse: {}", user);
        }
        log.debug("- createUser() added user: {}", valid);
        return user;
    }

    @Override
    public User update(User user) {
        log.debug("+ putResponse: {}", user);
        userValidator.validateUserHasId(users, user);
        log.debug("- putResponse: {}", user);

        return user;
    }

    @Override
    public void delete(User user) {
        users.remove(user.getId());
    }

    @Override
    public List<User> getAll() {
        log.debug("- getResponse: Текущее количество пользователей: {}", users.size());
        return new ArrayList<>(users.values());
    }

    public int getIdCounter() {
        return idCounter++;
    }
}
