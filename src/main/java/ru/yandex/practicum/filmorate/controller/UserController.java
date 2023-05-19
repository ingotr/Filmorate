package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.constants.Constants;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(Constants.USERS_PATH)
@Slf4j
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();
    private int idCounter = 1;

    @GetMapping
    public List<User> getAllUsers() {
        log.debug(Constants.RECEIVE_GET_REQUEST + Constants.USERS_PATH);
        log.debug(Constants.CURRENT_USER_COUNT, users.size());
        return List.copyOf(users.values());
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.debug(Constants.RECEIVE_POST_REQUEST + Constants.USERS_PATH);
        if (user != null) {
            isUserEmailValid(user);
        } else {
            log.warn(Constants.RECEIVED_EMPTY_REQUEST);
            throw new ValidationException(Constants.RECEIVED_EMPTY_REQUEST);
        }
        log.debug(Constants.ADDED_NEW_USER + user.getId());
        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.debug(Constants.RECEIVED_PUT_REQUEST + Constants.USERS_PATH);
        if (user != null) {
            isUpdatedUserHasId(user);
        } else {
            log.warn(Constants.RECEIVED_EMPTY_REQUEST);
            throw new ValidationException(Constants.RECEIVED_EMPTY_REQUEST);
        }
        log.debug(Constants.UPDATE_USER + user.getId());
        return user;
    }

    private void isUpdatedUserHasId(User user) {
        if (user.getId() != null && users.containsKey(user.getId())) {
            users.replace(user.getId(), user);
        } else {
            log.warn(Constants.USER_HAS_NO_ID);
            throw new ValidationException(Constants.USER_HAS_NO_ID + Constants.PLEASE_FIX_AND_TRY_AGAIN);
        }
        log.debug(Constants.UPDATE_USER + user.getId());
    }

    private void isUserEmailValid(User user) {
        if (!user.getEmail().isBlank()) {
            isUserLoginValid(user);
        } else {
            log.warn(Constants.USER_HAS_NO_EMAIL);
            throw new ValidationException(Constants.USER_HAS_NO_EMAIL + Constants.PLEASE_FIX_AND_TRY_AGAIN);
        }
    }

    private void isUserLoginValid(User user) {
        if (!user.getLogin().isBlank()) {
            isUserBirthdayValid(user);
        } else {
            log.warn(Constants.USER_HAS_NO_LOGIN);
            throw new ValidationException(Constants.USER_HAS_NO_LOGIN);
        }
    }

    private void isUserBirthdayValid(User user) {
        if (!user.getBirthday().isAfter(LocalDate.now())) {
            isUserNameBlank(user);
        } else {
            log.warn(Constants.USER_BIRTHDAY_IN_FUTURE);
            throw new ValidationException(Constants.USER_BIRTHDAY_IN_FUTURE);
        }
    }

    private void isUserNameBlank(User user) {
        try {
            if (user.getName().isBlank()) {
                user.setName(user.getLogin());
            }
        } catch (NullPointerException e) {
            user.setName(user.getLogin());
            log.warn(Constants.NEW_USER_EMPTY_NAME_REPLACED_BY_LOGIN);
        }
        setNewUserID(user);
    }

    private void setNewUserID(User user) {
        user.setId(getIdCounter());
        users.put(user.getId(), user);
    }

    public int getIdCounter() {
        return idCounter++;
    }
}
