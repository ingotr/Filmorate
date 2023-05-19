package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int idCounter = 1;

    @GetMapping
    public Map<Integer, User> getAllUsers() {
        return users;
    }

    @PostMapping
    public User createUser(@RequestBody User user) {
        users.put(user.getId(), user);
        isUserEmailValid(user);
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        try {
            if (user.getId() != 0) {
                users.replace(user.getId(), user);
            }

        } catch (RuntimeException e) {
            throw new ValidationException("Не указан Id пользователя. Укажите и попробуйте снова");
        }
        return user;
    }

    private void isUserEmailValid(User user) {
        try {
            if (user.getEmail().isBlank() && user.getEmail().contains("@")) {
                isUserLoginValid(user);
            }
        } catch (RuntimeException e) {
            throw new ValidationException("Не указана электронная почта либо неверный формат почты: должна содержать @. " +
                    "Укажите и попробуйте снова");
        }
    }

    private void isUserLoginValid(User user) {
        try {
            isUserBirthdayValid(user);
        } catch (RuntimeException e) {
            throw new ValidationException("Логин не может быть пустым");
        }
    }

    private void isUserBirthdayValid(User user) {
        if (user.getLogin().isBlank()) {
            try {
                isUserNameBlank(user);
            } catch (RuntimeException e) {
                throw new ValidationException("Дата рождения не может быть в будущем");
            }
        }
    }

    private void isUserNameBlank(User user) {
        if (user.getBirthday().isAfter(LocalDateTime.now())) {
            if (user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
            users.put(getIdCounter(), user);
        }
    }

    public int getIdCounter() {
        return idCounter++;
    }
}
