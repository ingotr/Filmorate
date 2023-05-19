package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDateTime;
import java.util.Map;

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
                .birthday(LocalDateTime.of(1993, 7, 12, 1, 1))
                .build();

        Map<Integer, User> users = userController.getAllUsers();
        User newUser = userController.createUser(user);
        assertNotNull(newUser, "Новый пользователь не найден");
        assertEquals(user, newUser, "Отправленный и добавленный пользователь отличаются");
        log.debug("users" + users);
        assertEquals(1, users.size(), "Число пользователей отличается от 1");

        //Не указана электронная почта либо неверный формат почты
        User userWithEmptyEmail = User.builder()
                .id(1)
                .email("")
                .login("ivanov93")
                .name("Иван")
                .birthday(LocalDateTime.of(1993, 7, 12, 1, 1))
                .build();

        assertThrows(ValidationException.class, () -> userController.createUser(userWithEmptyEmail),
                "Электронная почта не указана.");
        assertEquals(1, users.size(), "Число пользователей отличается от 1");

        //Пустой логин
        User userWithEmptyLogin = User.builder()
                .id(1)
                .email("ivanov@yandex.ru")
                .login("")
                .name("Иван")
                .birthday(LocalDateTime.of(1993, 7, 12, 1, 1))
                .build();

        assertThrows(ValidationException.class, () -> userController.createUser(userWithEmptyLogin),
                "Логин пустой.");
        assertEquals(1, users.size(), "Число пользователей больше 1");

        //Дата рождения не может быть в будущем
        User userWithBirthdayInFuture = User.builder()
                .id(1)
                .email("ivanov@yandex.ru")
                .login("ivanov93")
                .name("Иван")
                .birthday(LocalDateTime.of(2024, 5, 14, 1, 1))
                .build();

        assertThrows(ValidationException.class, () -> userController.createUser(userWithBirthdayInFuture),
                "Дата рождения пользователя в будущем.");
        assertEquals(1, users.size(), "Число пользователей больше 1");

        //Пустой запрос
        assertThrows(ValidationException.class, () -> userController.createUser(null),
                "Отправлен пустой запрос");
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
                .birthday(LocalDateTime.of(1993, 7, 12, 1, 1))
                .build();
        Map<Integer, User> users = userController.getAllUsers();
        assertThrows(ValidationException.class, () -> userController.updateUser(userWithEmptyId),
                "Не указан Id пользователя. Укажите и попробуйте снова");
        assertEquals(0, users.size(), "Число пользователей больше 0");

        //Пустой запрос
        assertThrows(ValidationException.class, () -> userController.updateUser(null),
                "Отправлен пустой запрос");
        assertEquals(0, users.size(), "Число пользователей больше 0");
    }

    @AfterEach
    void cleanUp() {
        userController = null;
    }
}