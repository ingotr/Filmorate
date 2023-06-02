package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.constants.Constants;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.storage.user.impl.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.validation.UserValidator;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
class UserControllerTest {

    private UserStorage userStorage;
    private UserService userService;
    private UserController userController;
    private ValidatorFactory factory;
    private UserValidator userValidator;
    private Validator validator;

    @AfterEach
    void cleanUp() {
        userValidator = null;
        userStorage = null;
        userService = null;
        userController = null;
        validator = null;
        factory = null;
    }

    @BeforeEach
    void setUp() {
        userValidator = new UserValidator();
        userStorage = new InMemoryUserStorage(userValidator);
        userService = new UserService(userStorage);
        userController = new UserController(userService);
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldAddUser() {
        Set<Long> friends = Set.of(2L, 3L, 4L);
        User user = new User("ivanov@yandex.ru", "ivanov93", LocalDate.of(1993, 7, 12),
                null, "Иван", friends);
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
        Set<Long> friends = Set.of(2L, 3L, 4L);
        User user = new User("ivanov@yandex.ru", "ivanov93", LocalDate.of(2024, 7, 12),
                null, "Иван", friends);
        User newUser = userController.addUser(user);
        Set<ConstraintViolation<User>> violations = validator.validate(newUser);
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldAddUserWhenFailLogin() {
        Set<Long> friends = Set.of(2L, 3L, 4L);
        User user = new User("ivanov@yandex.ru", null, LocalDate.of(1993, 7, 12),
                null, "Иван", friends);
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldAddUserWithEmptyName() {
        Set<Long> friends = Set.of(2L, 3L, 4L);
        User user = new User("ivanov@yandex.ru", "ivanov93", LocalDate.of(1993, 7, 12),
                null, null, friends);
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
        Set<Long> friends = Set.of(2L, 3L, 4L);
        User user = new User("ivanov@yandex.ru", "ivanov93", LocalDate.of(1993, 7, 12),
                null, "Иван", friends);
        List<User> users = userController.getAllUsers();
        assertThrows(ValidationException.class, () -> userController.updateUser(user),
                "Не указан Id пользователя");
        assertEquals(0, users.size(), "Число пользователей больше 0");
    }

    @Test
    void shouldUpdateUserWithUnknownId() {
        Set<Long> friends = Set.of(2L, 3L, 4L);
        User user = new User("ivanov@yandex.ru", "ivanov93", LocalDate.of(1993, 7, 12),
                9999, "Иван", friends);
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

    @Test
    void getById() {
        Set<Long> friends = Set.of(2L, 3L, 4L);
        User user = new User("ivanov@yandex.ru", "ivanov93", LocalDate.of(1993, 7, 12),
                null, "Иван", friends);
        userController.addUser(user);
        User userById = userController.getById(1);

        assertNotNull(userById, "Пользователь не найден");
        assertEquals(user, userById, "Пользователи отличаются");
    }

    @Test
    void addFriend() {
        Set<Long> friends = new HashSet<>();
        User userOne = new User("ivanov@yandex.ru", "ivanov93", LocalDate.of(1993, 7, 12),
                null, "Иван", friends);
        userController.addUser(userOne);
        User userTwo = new User("sidorov@yandex.ru", "sidoroveugen", LocalDate.of(1991, 4, 22),
                null, "Евгений", friends);
        userController.addUser(userTwo);
        userController.addFriend(1, 2);
        assertEquals(1, userOne.getFriends().size(), "Число друзей не равно 1");
        assertEquals(1, userTwo.getFriends().size(), "Число друзей не равно 1");
    }

    @Test
    void deleteFriend() {
        Set<Long> friends = new HashSet<>();
        User userOne = new User("ivanov@yandex.ru", "ivanov93", LocalDate.of(1993, 7, 12),
                null, "Иван", friends);
        userController.addUser(userOne);
        User userTwo = new User("sidorov@yandex.ru", "sidoroveugen", LocalDate.of(1991, 4, 22),
                null, "Евгений", friends);
        userController.addUser(userTwo);
        userController.addFriend(1, 2);
        userController.deleteFriend(1, 2);

        assertEquals(0, userOne.getFriends().size(), "Число друзей не равно 0");
        assertEquals(0, userTwo.getFriends().size(), "Число друзей не равно 0");
    }

    @Test
    void getFriends() {
        Set<Long> friends = new HashSet<>();
        User userOne = new User("ivanov@yandex.ru", "ivanov93", LocalDate.of(1993, 7, 12),
                null, "Иван", friends);
        userController.addUser(userOne);
        User userTwo = new User("sidorov@yandex.ru", "sidoroveugen", LocalDate.of(1991, 4, 22),
                null, "Евгений", friends);
        userController.addUser(userTwo);
        userController.addFriend(1, 2);

        assertEquals(1, userOne.getFriends().size(), "Число друзей не равно 1");
        List<User> friendsUserOne = userController.getFriends(1);
        log.debug("friends {}", userOne.getFriends());
        log.debug("friendList {}", friendsUserOne);
        assertEquals(1, friendsUserOne.size(), "Число друзей не равно 1");
    }

    @Test
    void getCommonFriends() {
        Set<Long> friends = new HashSet<>();
        User userOne = new User("ivanov@yandex.ru", "ivanov93", LocalDate.of(1993, 7, 12),
                null, "Иван", friends);
        userController.addUser(userOne);
        User userTwo = new User("sidorov@yandex.ru", "sidoroveugen", LocalDate.of(1991, 4, 22),
                null, "Евгений", friends);
        userController.addUser(userTwo);
        User userThree = new User("petrova@yandex.ru", "petrovadarya", LocalDate.of(1995, 1, 6),
                null, "Дарья", friends);
        userController.addUser(userThree);
        userController.addFriend(1, 2);
        userController.addFriend(1, 3);
        userController.addFriend(2, 3);

        List<User> commonFriends = userController.getCommonFriends(1, 3);
        assertEquals(1, commonFriends.size(), "Число общих друзей не равно 1");
        log.debug("Common friends: {}", commonFriends);
    }
}