package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.constants.Constants;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

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
class FilmControllerTest {
    private FilmController filmController;
    private ValidatorFactory factory;
    private Validator validator;

    @AfterEach
    void cleanUp() {
        filmController = null;
        validator = null;
        factory = null;
    }

    @BeforeEach
    void setUp() {
        filmController = new FilmController();
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldAddFilm() {
        Film film = new Film("Криминальное чтиво", "Фильм Квентина Тарантино", LocalDate.of(1994, 5, 24), 154, null);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertTrue(violations.isEmpty());

        Film newFilm = filmController.add(film);
        List<Film> films = filmController.getAllFilms();
        assertNotNull(newFilm, Constants.NEW_FILM_NOT_FOUND);
        assertEquals(film, newFilm, Constants.RECEIVED_FILM_AND_NEW_FILM_DIFFERENT);
        assertEquals(1, films.size(), "Число фильмов отличается от 1");
    }

    @Test
    void shouldAddFilmWithEmptyName() {
        Film film = new Film("", "Фильм Квентина Тарантино", LocalDate.of(1994, 5, 24), 154, null);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldAddFilmWithLongDescription() {
        Film film = new Film("Криминальное чтиво", Constants.LOREM_IPSUM, LocalDate.of(1994, 5, 24), 154, null);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldAddFilmWithInvalidReleaseDate() {
        Film film = new Film("Криминальное чтиво", "Фильм Квентина Тарантино", LocalDate.of(1844, 5, 24), 154, null);
        List<Film> films = filmController.getAllFilms();

        assertThrows(ValidationException.class, () -> filmController.add(film),
                "Дата релиза — не может быть раньше 28 декабря 1895 года.");
        assertEquals(0, films.size(), "Число фильмов отличается от 0");
    }

    @Test
    void shouldAddFilmWithInvalidDuration() {
        Film film = new Film("Криминальное чтиво", "Фильм Квентина Тарантино", LocalDate.of(1994, 5, 24), -2, null);
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldAddFilmWhenEmptyRequestBody() {
        List<Film> films = filmController.getAllFilms();

        assertThrows(ValidationException.class, () -> filmController.add(null),
                "Получен пустой запрос");
        assertEquals(0, films.size(), "Число фильмов отличается от 0");
    }

    @Test
    void shouldUpdateFilmWithEmptyId() {
        Film film = new Film("Криминальное чтиво", "Фильм Квентина Тарантино", LocalDate.of(1994, 5, 24), 154, null);
        List<Film> films = filmController.getAllFilms();

        assertThrows(ValidationException.class, () -> filmController.update(film),
                "Не указан Id фильма");
        assertEquals(0, films.size(), "Число фильмов больше 0");
    }

    @Test
    void shouldUpdateFilmWithEmptyRequest() {
        List<Film> films = filmController.getAllFilms();
        assertThrows(NullPointerException.class, () -> filmController.update(null),
                "Получен пустой запрос");
        assertEquals(0, films.size(), "Число фильмов больше 0");
    }
}