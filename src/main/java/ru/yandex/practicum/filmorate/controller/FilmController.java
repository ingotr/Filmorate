package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.validation.FilmValidator;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();
    private final FilmValidator filmValidator;
    private int idCounter = 1;

    @Autowired
    public FilmController() {
        this.filmValidator = new FilmValidator();
    }

    @GetMapping
    public List<Film> getAllFilms() {
        log.debug("- getResponse: Текущее количество фильмов: {}", films.size());

        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        log.debug("+ postResponse: {}", film);
        boolean valid = filmValidator.validate(film);
        if (valid) {
            film.setId(getIdCounter());
            films.put(film.getId(), film);
            log.debug("- postResponse: {}", film);
        }
        log.debug("- createFilm() add film: {}", valid);

        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.debug("+ putResponse: {}", film);
        filmValidator.validateFilmHasId(films, film);
        log.debug("- putResponse: {}", film);

        return film;
    }

    public int getIdCounter() {
        return idCounter++;
    }
}
