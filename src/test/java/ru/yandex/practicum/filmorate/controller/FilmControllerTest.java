package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

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
    void shouldCreateFilm() {
        //Стандартный кейс
        Film film = Film.builder()
                .id(1)
                .title("Криминальное чтиво")
                .description("Фильм Квентина Тарантино")
                .releaseDate(LocalDateTime.of(1994, 5, 24, 1, 1))
                .duration(154)
                .build();

        Map<Integer, Film> films = filmController.getAllFilms();
        Film newFilm = filmController.createFilm(film);
        assertNotNull(newFilm, "Новый фильм не найден");
        assertEquals(film, newFilm, "Отправленный и добавленный фильм отличаются");
        assertEquals(1, films.size(), "Число фильмов больше 1");

        //Не указано название фильма
        Film filmWithEmptyTitle = Film.builder()
                .id(2)
                .title("")
                .description("Фильм Квентина Тарантино")
                .releaseDate(LocalDateTime.of(1994, 5, 24, 1, 1))
                .duration(154)
                .build();
        assertThrows(ValidationException.class, () -> filmController.createFilm(filmWithEmptyTitle),
                "Не указано название фильма. Укажите и попробуйте снова");
        assertEquals(1, films.size(), "Число фильмов больше 1");

        //todo Длина описания больше 200 символов или не указано

        //todo Дата релиза раньше 28 декабря 1895 года или не указана
        //todo Продолжительность фильма отрицательная или равна 0
        //todo Пустой запрос??
    }

    @Test
    void shouldUpdateFilm() {
        //todo Не указан Id фильма
        //todo Пустой запрос??
    }

    @AfterEach
    void cleanUp() {
        filmController = null;
    }
}