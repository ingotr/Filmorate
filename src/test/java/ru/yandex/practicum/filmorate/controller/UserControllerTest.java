package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import ru.yandex.practicum.filmorate.constants.Constants;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class UserControllerTest {

    public static UserController userController = new UserController();

    @AfterAll
    static void tearDown() {
        userController = null;
    }

    @BeforeEach
    void setUp() {
        userController = new UserController();
    }

    @Test
    @DisplayName("POST user")
    void shouldCreateUser() {
        //Стандартный кейс
        User user = User.builder()
                .id(1)
                .email("ivanov@yandex.ru")
                .login("ivanov93")
                .name("Иван")
                .birthday(LocalDate.of(1993, 7, 12))
                .build();

        User newUser = userController.createUser(user);
        List<User> users = userController.getAllUsers();
        assertNotNull(newUser, Constants.NEW_USER_NOT_FOUND);
        assertEquals(user, newUser, Constants.RECEIVED_AND_NEW_USER_DIFFERENT);
        log.debug("users" + users);
        assertEquals(1, users.size(), "Число пользователей отличается от 1");

        //Не указана электронная почта либо неверный формат почты
        User userWithEmptyEmail = User.builder()
                .id(1)
                .email("")
                .login("ivanov93")
                .name("Иван")
                .birthday(LocalDate.of(1993, 7, 12))
                .build();

        assertThrows(ValidationException.class, () -> userController.createUser(userWithEmptyEmail),
                Constants.USER_HAS_NO_EMAIL);
        assertEquals(1, users.size(), "Число пользователей отличается от 1");

        //Пустой логин
        User userWithEmptyLogin = User.builder()
                .id(1)
                .email("ivanov@yandex.ru")
                .login("")
                .name("Иван")
                .birthday(LocalDate.of(1993, 7, 12))
                .build();

        assertThrows(ValidationException.class, () -> userController.createUser(userWithEmptyLogin),
                Constants.USER_HAS_NO_LOGIN);
        assertEquals(1, users.size(), "Число пользователей больше 1");

        //Дата рождения не может быть в будущем
        User userWithBirthdayInFuture = User.builder()
                .id(1)
                .email("ivanov@yandex.ru")
                .login("ivanov93")
                .name("Иван")
                .birthday(LocalDate.of(2024, 5, 14))
                .build();

        assertThrows(ValidationException.class, () -> userController.createUser(userWithBirthdayInFuture),
                Constants.USER_BIRTHDAY_IN_FUTURE);
        assertEquals(1, users.size(), "Число пользователей больше 1");

        //Пустой запрос
        assertThrows(ValidationException.class, () -> userController.createUser(null),
                Constants.RECEIVED_EMPTY_REQUEST);
        assertEquals(1, users.size(), "Число пользователей больше 1");

    }

    @Test
    @DisplayName("PUT user")
    void shouldUpdateUser() {
        //Не указан Id пользователя
        User userWithEmptyId = User.builder()
                .email("ivanov@yandex.ru")
                .login("ivanov93")
                .name("Иван")
                .birthday(LocalDate.of(1993, 7, 12))
                .build();
        List<User> users = userController.getAllUsers();
        assertThrows(ValidationException.class, () -> userController.updateUser(userWithEmptyId),
                Constants.USER_HAS_NO_ID);
        assertEquals(0, users.size(), "Число пользователей больше 0");

        //Пустой запрос
        assertThrows(ValidationException.class, () -> userController.updateUser(null),
                Constants.RECEIVED_EMPTY_REQUEST);
        assertEquals(0, users.size(), "Число пользователей больше 0");
    }

    @AfterEach
    void cleanUp() {
        userController = null;
    }
}