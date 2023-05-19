package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int idCounter = 1;

    @GetMapping
    public Map<Integer, User> getAllUsers() {
        log.debug("Получен запрос GET /users.");
        log.debug("Текущее количество пользователей: {}", users.size());
        return users;
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        log.debug("Получен запрос POST /users.");
        if (user != null) {
            isUserEmailValid(user);
        } else {
            log.warn("Отправлен пустой запрос");
            throw new ValidationException("Отправлен пустой запрос");
        }
        log.debug("Добавлен новый пользователь с Id: " + user.getId());
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        log.debug("Получен запрос PUT /users.");
        if (user != null) {
            isUpdatedUserHasId(user);
        } else {
            log.warn("Отправлен пустой запрос");
            throw new ValidationException("Отправлен пустой запрос");
        }
        log.debug("Обновлен пользователь с Id: " + user.getId());
        return user;
    }

    private void isUpdatedUserHasId(User user) {
        if (user.getId() != null) {
            users.replace(user.getId(), user);
        } else {
            log.warn("Не указан Id пользователя");
            throw new ValidationException("Не указан Id пользователя. Укажите и попробуйте снова");
        }
        log.debug("Обновлен пользователь с Id: " + user.getId());
    }

    private void isUserEmailValid(User user) {
        if (!user.getEmail().isBlank() && user.getEmail().contains("@")) {
            isUserLoginValid(user);
        } else {
            log.warn("Не указана электронная почта либо неверный формат почты");
            throw new ValidationException("Не указана электронная почта либо неверный формат почты: должна содержать @. " +
                    "Укажите и попробуйте снова");
        }
    }

    private void isUserLoginValid(User user) {
        if (!user.getLogin().isBlank()) {
            isUserBirthdayValid(user);
        } else {
            log.warn("Пустой логин");
            throw new ValidationException("Логин не может быть пустым");
        }
    }

    private void isUserBirthdayValid(User user) {
        if (!user.getBirthday().isAfter(LocalDateTime.now())) {
            isUserNameBlank(user);
        } else {
            log.warn("Дата рождения не может быть в будущем");
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }

    private void isUserNameBlank(User user) {
        if (user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(getIdCounter(), user);
    }

    public int getIdCounter() {
        return idCounter++;
    }
}
