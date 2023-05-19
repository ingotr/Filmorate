package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class FilmControllerTest {
    public static FilmController filmController = new FilmController();

    @AfterAll
    static void tearDown() {
        filmController = null;
    }

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
    }

    @Test
    @DisplayName("POST film")
    void shouldCreateFilm() {
        //Стандартный кейс
        Film film = Film.builder()
                .id(1)
                .name("Криминальное чтиво")
                .description("Фильм Квентина Тарантино")
                .releaseDate(Optional.of(LocalDate.of(1994, 5, 24)))
                .duration(154)
                .build();

        Film newFilm = filmController.createFilm(film);
        List<Film> films = filmController.getAllFilms();
        assertNotNull(newFilm, "Новый фильм не найден");
        assertEquals(film, newFilm, "Отправленный и добавленный фильм отличаются");
        assertEquals(1, films.size(), "Число фильмов больше 1");

        //Не указано название фильма
        Film filmWithEmptyTitle = Film.builder()
                .id(2)
                .name("")
                .description("Фильм Квентина Тарантино")
                .releaseDate(Optional.of(LocalDate.of(1994, 5, 24)))
                .duration(154)
                .build();
        assertThrows(ValidationException.class, () -> filmController.createFilm(filmWithEmptyTitle),
                "Не указано название фильма. Укажите и попробуйте снова");
        assertEquals(1, films.size(), "Число фильмов больше 1");

        //Длина описания больше 200 символов
        String description = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, \n" +
                "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. \n" +
                "Ut enim ad minim veniam, quis nostrud exercitation ullamco aboris " +
                "nisi ut aliquip ex ea commodo consequat.";
        Film filmWithLongDescription = Film.builder()
                .id(3)
                .name("Криминальное чтиво")
                .description(description)
                .releaseDate(Optional.of(LocalDate.of(1994, 5, 24)))
                .duration(154)
                .build();
        assertThrows(ValidationException.class, () -> filmController.createFilm(filmWithLongDescription),
                "Максимальная длина описания - 200 символов.");
        assertEquals(1, films.size(), "Число фильмов больше 1");

        //Описание фильма не указано
        Film filmWithEmptyDescription = Film.builder()
                .id(3)
                .name("Криминальное чтиво")
                .description("")
                .releaseDate(Optional.of(LocalDate.of(1994, 5, 24)))
                .duration(154)
                .build();
        assertThrows(ValidationException.class, () -> filmController.createFilm(filmWithEmptyDescription),
                "Описание пустое.");
        assertEquals(1, films.size(), "Число фильмов больше 1");

        //Дата релиза раньше 28 декабря 1895 года
        Film filmBeforeValidDate = Film.builder()
                .id(1)
                .name("Криминальное чтиво")
                .description("Фильм Квентина Тарантино")
                .releaseDate(Optional.of(LocalDate.of(1844, 5, 24)))
                .duration(154)
                .build();

        assertThrows(ValidationException.class, () -> filmController.createFilm(filmBeforeValidDate),
                "Дата релиза — не может быть раньше 28 декабря 1895 года");
        assertEquals(1, films.size(), "Число фильмов больше 1");

        //Дата релиза не указана
        Film filmNullReleaseDate = Film.builder()
                .id(1)
                .name("Криминальное чтиво")
                .description("Фильм Квентина Тарантино")
                .duration(154)
                .build();

        assertThrows(NullPointerException.class, () -> filmController.createFilm(filmNullReleaseDate),
                "Дата релиза — не указана");
        assertEquals(1, films.size(), "Число фильмов больше 1");

        //Продолжительность фильма отрицательная
        Film filmValidDuration = Film.builder()
                .id(1)
                .name("Криминальное чтиво")
                .description("Фильм Квентина Тарантино")
                .releaseDate(Optional.of(LocalDate.of(1844, 5, 24)))
                .duration(-2)
                .build();

        assertThrows(ValidationException.class, () -> filmController.createFilm(filmValidDuration),
                "Продолжительность фильма должна быть положительной");
        assertEquals(1, films.size(), "Число фильмов больше 1");

        //Продолжительность равна 0
        Film filmZeroDuration = Film.builder()
                .id(1)
                .name("Криминальное чтиво")
                .description("Фильм Квентина Тарантино")
                .releaseDate(Optional.of(LocalDate.of(1844, 5, 24)))
                .duration(0)
                .build();

        assertThrows(ValidationException.class, () -> filmController.createFilm(filmZeroDuration),
                "Продолжительность фильма должна быть положительной");
        assertEquals(1, films.size(), "Число фильмов больше 1");

        //Пустой запрос
        assertThrows(ValidationException.class, () -> filmController.createFilm(null),
                "Отправлен пустой запрос");
        assertEquals(1, films.size(), "Число фильмов больше 1");
    }

    @Test
    @DisplayName("PUT film")
    void shouldUpdateFilm() {
        //Не указан Id фильма
        Film filmWithEmptyId = Film.builder()
                .name("Криминальное чтиво")
                .description("Фильм Квентина Тарантино")
                .releaseDate(Optional.of(LocalDate.of(1994, 5, 24)))
                .duration(154)
                .build();
        List<Film> films = filmController.getAllFilms();
        assertThrows(ValidationException.class, () -> filmController.updateFilm(filmWithEmptyId),
                "Не указан Id фильма. Укажите и попробуйте снова");
        assertEquals(0, films.size(), "Число фильмов больше 0");

        //Пустой запрос
        assertThrows(ValidationException.class, () -> filmController.updateFilm(null),
                "Отправлен пустой запрос");
        assertEquals(0, films.size(), "Число фильмов больше 0");
    }

    @AfterEach
    void cleanUp() {
        filmController = null;
    }
}