package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import ru.yandex.practicum.filmorate.constants.Constants;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;

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
                .releaseDate(LocalDate.of(1994, 5, 24))
                .duration(154)
                .build();

        Film newFilm = filmController.createFilm(film);
        List<Film> films = filmController.getAllFilms();
        assertNotNull(newFilm, Constants.NEW_FILM_NOT_FOUND);
        assertEquals(film, newFilm, Constants.RECEIVED_FILM_AND_NEW_FILM_DIFFERENT);
        assertEquals(1, films.size(), "Число фильмов больше 1");

        //Не указано название фильма
        Film filmWithEmptyTitle = Film.builder()
                .id(2)
                .name("")
                .description("Фильм Квентина Тарантино")
                .releaseDate(LocalDate.of(1994, 5, 24))
                .duration(154)
                .build();
        assertThrows(ValidationException.class, () -> filmController.createFilm(filmWithEmptyTitle),
                Constants.FILM_HAS_NO_NAME + Constants.PLEASE_FIX_AND_TRY_AGAIN);
        assertEquals(1, films.size(), "Число фильмов больше 1");

        //Длина описания больше 200 символов
        Film filmWithLongDescription = Film.builder()
                .id(3)
                .name("Криминальное чтиво")
                .description(Constants.LOREM_IPSUM)
                .releaseDate(LocalDate.of(1994, 5, 24))
                .duration(154)
                .build();
        assertThrows(ValidationException.class, () -> filmController.createFilm(filmWithLongDescription),
                Constants.FILM_MAX_DESCRIPTION_LENGTH);
        assertEquals(1, films.size(), "Число фильмов больше 1");

        //Описание фильма не указано
        Film filmWithEmptyDescription = Film.builder()
                .id(3)
                .name("Криминальное чтиво")
                .description("")
                .releaseDate(LocalDate.of(1994, 5, 24))
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
                .releaseDate(LocalDate.of(1844, 5, 24))
                .duration(154)
                .build();

        assertThrows(ValidationException.class, () -> filmController.createFilm(filmBeforeValidDate),
                Constants.FILM_INVALID_RELEASE_DATE);
        assertEquals(1, films.size(), "Число фильмов больше 1");

        //Дата релиза не указана
        Film filmNullReleaseDate = Film.builder()
                .id(1)
                .name("Криминальное чтиво")
                .description("Фильм Квентина Тарантино")
                .duration(154)
                .build();

        assertThrows(ValidationException.class, () -> filmController.createFilm(filmNullReleaseDate),
                Constants.FILM_HAS_NO_RELEASE_DATE);
        assertEquals(1, films.size(), "Число фильмов больше 1");

        //Продолжительность фильма отрицательная
        Film filmValidDuration = Film.builder()
                .id(1)
                .name("Криминальное чтиво")
                .description("Фильм Квентина Тарантино")
                .releaseDate(LocalDate.of(1844, 5, 24))
                .duration(-2)
                .build();

        assertThrows(ValidationException.class, () -> filmController.createFilm(filmValidDuration),
                Constants.FILM_DURATION_INVALID);
        assertEquals(1, films.size(), "Число фильмов больше 1");

        //Продолжительность равна 0
        Film filmZeroDuration = Film.builder()
                .id(1)
                .name("Криминальное чтиво")
                .description("Фильм Квентина Тарантино")
                .releaseDate(LocalDate.of(1844, 5, 24))
                .duration(0)
                .build();

        assertThrows(ValidationException.class, () -> filmController.createFilm(filmZeroDuration),
                Constants.FILM_DURATION_INVALID);
        assertEquals(1, films.size(), "Число фильмов больше 1");

        //Пустой запрос
        assertThrows(ValidationException.class, () -> filmController.createFilm(null),
                Constants.RECEIVED_EMPTY_REQUEST);
        assertEquals(1, films.size(), "Число фильмов больше 1");
    }

    @Test
    @DisplayName("PUT film")
    void shouldUpdateFilm() {
        //Не указан Id фильма
        Film filmWithEmptyId = Film.builder()
                .name("Криминальное чтиво")
                .description("Фильм Квентина Тарантино")
                .releaseDate(LocalDate.of(1994, 5, 24))
                .duration(154)
                .build();
        List<Film> films = filmController.getAllFilms();
        assertThrows(ValidationException.class, () -> filmController.updateFilm(filmWithEmptyId),
                Constants.FILM_HAS_NO_ID);
        assertEquals(0, films.size(), "Число фильмов больше 0");

        //Пустой запрос
        assertThrows(ValidationException.class, () -> filmController.updateFilm(null),
                Constants.RECEIVED_EMPTY_REQUEST);
        assertEquals(0, films.size(), "Число фильмов больше 0");
    }

    @AfterEach
    void cleanUp() {
        filmController = null;
    }
}