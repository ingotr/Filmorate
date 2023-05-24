package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.validation.UserValidator;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private final UserValidator userValidator;
    private int idCounter = 1;

    @Autowired
    public UserController() {
        this.userValidator = new UserValidator();
    }

    @GetMapping
    public List<User> getAllUsers() {
        log.debug("- getResponse: Текущее количество пользователей: {}", users.size());

        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User addUser(@Valid @RequestBody User user) {
        log.debug("+ getResponse: {}", user);
        boolean valid = userValidator.validate(user);
        if (valid) {
            user.setId(getIdCounter());
            users.put(user.getId(), user);
            log.debug("- getResponse: {}", user);
        }
        log.debug("- createUser() added film: {}", valid);

        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.debug("+ putResponse: {}", user);
        userValidator.validateUserHasId(users, user);
        log.debug("- putResponse: {}", user);

        return user;
    }

    public int getIdCounter() {
        return idCounter++;
    }
}
