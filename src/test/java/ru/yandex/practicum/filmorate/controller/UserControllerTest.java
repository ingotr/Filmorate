package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.constants.Constants;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class UserControllerTest {

    private UserController userController;
    private ValidatorFactory factory;
    private Validator validator;

    @AfterEach
    void cleanUp() {
        userController = null;
        validator = null;
        factory = null;
    }

    @BeforeEach
    void setUp() {
        userController = new UserController();
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldAddUser() {
        User user = new User("ivanov@yandex.ru", "ivanov93", LocalDate.of(1993, 7, 12), null, "Иван");
        User newUser = userController.addUser(user);
        List<User> users = userController.getAllUsers();
        Set<ConstraintViolation<User>> violations = validator.validate(newUser);
        assertTrue(violations.isEmpty());

        assertNotNull(newUser, Constants.NEW_USER_NOT_FOUND);
        assertEquals(user, newUser, Constants.RECEIVED_AND_NEW_USER_DIFFERENT);
        log.debug("users" + users);
        assertEquals(1, users.size(), "Число пользователей отличается от 1");
    }

    @Test
    void shouldAddUserWhenBirthdayInFuture() {
        User user = new User("ivanov@yandex.ru", "ivanov93", LocalDate.of(2024, 7, 12), null, "Иван");
        User newUser = userController.addUser(user);
        Set<ConstraintViolation<User>> violations = validator.validate(newUser);
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldAddUserWhenFailLogin() {
        User user = new User("ivanov@yandex.ru", null, LocalDate.of(1993, 7, 12), null, "Иван");
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldAddUserWithEmptyName() {
        User user = new User("ivanov@yandex.ru", "ivanov93", LocalDate.of(1993, 7, 12), null, null);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertTrue(violations.isEmpty());
        userController.addUser(user);

        List<User> users = userController.getAllUsers();
        assertEquals(1, users.size(), "Число пользователей отличается от 1");
    }

    @Test
    void shouldAddUserWhenGetEmptyRequestBody() {
        List<User> users = userController.getAllUsers();

        assertThrows(ValidationException.class, () -> userController.addUser(null),
                "Получен пустой запрос");
        assertEquals(0, users.size(), "Число пользователей отличается от 0");
    }

    @Test
    void shouldUpdateUserWithEmptyId() {
        User user = new User("ivanov@yandex.ru", "ivanov93", LocalDate.of(1993, 7, 12), null, "Иван");
        List<User> users = userController.getAllUsers();
        assertThrows(ValidationException.class, () -> userController.updateUser(user),
                "Не указан Id пользователя");
        assertEquals(0, users.size(), "Число пользователей больше 0");
    }

    @Test
    void shouldUpdateUserWithUnknownId() {
        User user = new User("ivanov@yandex.ru", "ivanov93", LocalDate.of(1993, 7, 12), 9999, "Иван");
        List<User> users = userController.getAllUsers();
        assertThrows(ValidationException.class, () -> userController.updateUser(user),
                "Не указан Id пользователя");
        assertEquals(0, users.size(), "Число пользователей больше 0");
    }

    @Test
    void shouldUpdateUserWithEmptyRequestBody() {
        User user = null;
        assertThrows(NullPointerException.class, () -> userController.updateUser(null),
                "Получен пустой запрос");
        List<User> users = userController.getAllUsers();

        assertEquals(0, users.size(), "Число пользователей больше 0");
    }
}