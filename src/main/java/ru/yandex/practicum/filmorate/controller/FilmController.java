package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private int idCounter = 1;

    @GetMapping
    public Map<Integer, Film> getAllFilms() {
        log.debug("Получен запрос GET /films.");
        log.debug("Текущее количество фильмов: {}", films.size());
        return films;
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) {
        log.debug("Получен запрос POST /films.");
        isFilmTitleValid(film);
        log.debug("Добавлен новый фильм с Id: " + film.getId());
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        log.debug("Получен запрос PUT /films.");
        try {
            if (film.getId() != 0) {
                films.replace(film.getId(), film);
            }

        } catch (RuntimeException e) {
            log.warn("Не указан Id фильма");
            throw new ValidationException("Не указан Id фильма. Укажите и попробуйте снова");
        }
        log.debug("Обновлен фильм с Id: " + film.getId());
        return film;
    }

    private void isFilmTitleValid(Film film) {
        try {
            if (!film.getName().isBlank()) {
                isFilmDescriptionLengthValid(film);
            }
        } catch (RuntimeException e) {
            log.warn("Не указано название фильма");
            throw new ValidationException("Не указано название фильма. Укажите и попробуйте снова");
        }
    }

    private void isFilmDescriptionLengthValid(Film film) {
        try {
            if (film.getDescription().length() <= 200) {
                isFilmReleaseDateValid(film);
            }
        } catch (RuntimeException e) {
            log.warn("Длина описания больше 200 символов");
            throw new ValidationException("Максимальная длина описания - 200 символов.");
        }
    }

    private void isFilmReleaseDateValid(Film film) {
        try {
            if (!film.getReleaseDate().isBefore(LocalDateTime.of(1895, 12, 28, 1, 1))) {
                isFilmDurationPositive(film);
            }
        } catch (RuntimeException e) {
            log.warn("Дата релиза раньше 28 декабря 1895 года");
            throw new ValidationException("Дата релиза — не может быть раньше 28 декабря 1895 года");
        }
    }

    private void isFilmDurationPositive(Film film) {
        try {
            if (film.getDuration() > 0) {
                films.put(getIdCounter(), film);
            }
        } catch (RuntimeException e) {
            log.warn("Продолжительность фильма отрицательная или равна 0");
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
    }

    public int getIdCounter() {
        return idCounter++;
    }
}
