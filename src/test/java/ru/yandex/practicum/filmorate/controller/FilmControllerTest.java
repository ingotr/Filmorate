package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.constants.Constants;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.impl.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.validation.FilmValidator;

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
class FilmControllerTest {
    private FilmValidator filmValidator;
    private FilmStorage filmStorage;
    private FilmService filmService;
    private FilmController filmController;
    private ValidatorFactory factory;
    private Validator validator;

    @AfterEach
    void cleanUp() {
        filmStorage = null;
        filmService = null;
        filmController = null;
        validator = null;
        factory = null;
    }

    @BeforeEach
    void setUp() {
        filmValidator = new FilmValidator();
        filmStorage = new InMemoryFilmStorage(filmValidator);
        filmService = new FilmService(filmStorage);
        filmController = new FilmController(filmService);
        factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @Test
    void shouldAddFilm() {
        Film film = new Film("Криминальное чтиво", "Фильм Квентина Тарантино",
                LocalDate.of(1994, 5, 24), 154, null, null);
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
        Film film = new Film("", "Фильм Квентина Тарантино", LocalDate.of(1994, 5, 24),
                154, null, new HashSet<>());
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldAddFilmWithLongDescription() {
        Film film = new Film("Криминальное чтиво", Constants.LOREM_IPSUM, LocalDate.of(1994, 5, 24),
                154, null, new HashSet<>());
        Set<ConstraintViolation<Film>> violations = validator.validate(film);
        assertFalse(violations.isEmpty());
    }

    @Test
    void shouldAddFilmWithInvalidReleaseDate() {
        Film film = new Film("Криминальное чтиво", "Фильм Квентина Тарантино", LocalDate.of(1844, 5, 24),
                154, null, new HashSet<>());
        List<Film> films = filmController.getAllFilms();

        assertThrows(ValidationException.class, () -> filmController.add(film),
                "Дата релиза — не может быть раньше 28 декабря 1895 года.");
        assertEquals(0, films.size(), "Число фильмов отличается от 0");
    }

    @Test
    void shouldAddFilmWithInvalidDuration() {
        Film film = new Film("Криминальное чтиво", "Фильм Квентина Тарантино", LocalDate.of(1994, 5, 24),
                -2, null, new HashSet<>());
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
        Film film = new Film("Криминальное чтиво", "Фильм Квентина Тарантино", LocalDate.of(1994, 5, 24),
                154, null, new HashSet<>());
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

    @Test
    void shouldGetById() {
        Film film = new Film("Криминальное чтиво", "Фильм Квентина Тарантино",
                LocalDate.of(1994, 5, 24), 154, null, null);
        filmController.add(film);
        Optional<Film> filmById = filmController.getById(1);

        assertNotNull(filmById, "Фильм не найден");
        assertTrue(filmById.isPresent(), "Фильм не найден");
        Film actualFilm = filmById.get();
        assertEquals(film, actualFilm, "Фильмы отличаются");
    }

    @Test
    void shouldAddLike() {
        Film film = new Film("Криминальное чтиво", "Фильм Квентина Тарантино",
                LocalDate.of(1994, 5, 24), 154, null, null);
        Film addedFilm = filmController.add(film);
        filmController.addLike(addedFilm.getId(), 1);
        assertEquals(1, addedFilm.getLikes().size(), "Число лайков не равно 1");
    }

    @Test
    void shouldDeleteLike() {
        Film film = new Film("Криминальное чтиво", "Фильм Квентина Тарантино",
                LocalDate.of(1994, 5, 24), 154, null, null);
        Film addedFilm = filmController.add(film);
        filmController.addLike(addedFilm.getId(), 1);
        filmController.deleteLike(addedFilm.getId(), 1);
        assertEquals(0, addedFilm.getLikes().size(), "Число лайков не равно 0");
    }

    @Test
    void shouldGetPopularFilms() {
        Film filmOne = new Film("Криминальное чтиво", "Фильм Квентина Тарантино",
                LocalDate.of(1994, 5, 24), 154, null, null);
        Film addedFilm = filmController.add(filmOne);
        filmController.addLike(addedFilm.getId(), 1);
        filmController.addLike(addedFilm.getId(), 2);
        Film filmTwo = new Film("Криминальное чтиво 2", "Фильм Квентина Тарантино",
                LocalDate.of(2004, 5, 24), 104, null, null);
        Film addedFilmTwo = filmController.add(filmTwo);
        filmController.addLike(addedFilmTwo.getId(), 1);

        List<Film> popularFilms = filmController.getPopularFilms(2);
        assertEquals(2, popularFilms.size(), "Число популярных фильмов не равно 2");
    }
}