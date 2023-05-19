package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.constants.Constants;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(Constants.FILMS_PATH)
@Slf4j
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private int idCounter = 1;

    @GetMapping
    public List<Film> getAllFilms() {
        log.debug(Constants.RECEIVE_GET_REQUEST + Constants.FILMS_PATH);
        log.debug(Constants.CURRENT_FILM_COUNT, films.size());
        return List.copyOf(films.values());
    }

    @PostMapping
    public Film createFilm(@Valid @RequestBody Film film) {
        log.debug(Constants.RECEIVE_POST_REQUEST + Constants.FILMS_PATH);
        if (film != null) {
            isFilmTitleValid(film);
        } else {
            log.warn(Constants.RECEIVED_EMPTY_REQUEST);
            throw new ValidationException(Constants.RECEIVED_EMPTY_REQUEST);
        }
        log.debug(Constants.ADDED_NEW_FILM + film.getId());
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.debug(Constants.RECEIVED_PUT_REQUEST + Constants.FILMS_PATH);
        if (film != null) {
            isUpdatedFilmHasId(film);
        } else {
            log.warn(Constants.RECEIVED_EMPTY_REQUEST);
            throw new ValidationException(Constants.RECEIVED_EMPTY_REQUEST);
        }

        return film;
    }

    private void isUpdatedFilmHasId(Film film) {
        if (film.getId() != null && films.containsKey(film.getId())) {
            films.replace(film.getId(), film);
        } else {
            log.warn(Constants.FILM_HAS_NO_ID);
            throw new ValidationException(Constants.FILM_HAS_NO_ID + Constants.PLEASE_FIX_AND_TRY_AGAIN);
        }
        log.debug(Constants.UPDATED_FILM + film.getId());
    }

    private void isFilmTitleValid(Film film) {
        if (!film.getName().isBlank()) {
            isFilmDescriptionLengthValid(film);
        } else {
            log.warn(Constants.FILM_HAS_NO_NAME);
            throw new ValidationException(Constants.FILM_HAS_NO_NAME + Constants.PLEASE_FIX_AND_TRY_AGAIN);
        }
    }

    private void isFilmDescriptionLengthValid(Film film) {
        if (film.getDescription().length() < 200 && !film.getDescription().isBlank()) {
            isFilmReleaseDateNotEmpty(film);
        } else {
            log.warn(Constants.FILM_MAX_DESCRIPTION_LENGTH);
            throw new ValidationException(Constants.FILM_MAX_DESCRIPTION_LENGTH);
        }
    }

    private void isFilmReleaseDateNotEmpty(Film film) {
        if (film.getReleaseDate().isPresent()) {
            isFilmReleaseDateValid(film);
        } else {
            log.warn(Constants.FILM_HAS_NO_RELEASE_DATE);
        }
    }

    private void isFilmReleaseDateValid(Film film) {
        if (!film.getReleaseDate().get().isBefore(LocalDate.of(1895, 12, 28))) {
            isFilmDurationPositive(film);
        } else {
            log.warn(Constants.FILM_INVALID_RELEASE_DATE);
            throw new ValidationException(Constants.FILM_INVALID_RELEASE_DATE);
        }
    }

    private void isFilmDurationPositive(Film film) {
        if (film.getDuration() > 0) {
            setNewFilmID(film);
            films.put(film.getId(), film);
        } else {
            log.warn(Constants.FILM_DURATION_INVALID);
            throw new ValidationException(Constants.FILM_DURATION_INVALID);
        }
    }

    private void setNewFilmID(Film film) {
        film.setId(getIdCounter());
        films.put(film.getId(), film);
    }

    public int getIdCounter() {
        return idCounter++;
    }
}
