package ru.yandex.practicum.filmorate.storage.film.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.validation.FilmValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();
    private FilmValidator filmValidator;
    private int idCounter = 1;

    @Override
    public Film add(Film film) {
        boolean valid = filmValidator.validate(film);
        if (valid) {
            film.setId(getIdCounter());
            films.put(film.getId(), film);
            log.debug("- postResponse: {}", film);
        }
        log.debug("- createFilm() add film: {}", valid);

        return film;
    }

    @Override
    public Film update(Film film) {
        filmValidator.validateFilmHasId(films, film);
        log.debug("- putResponse: {}", film);
        return film;
    }

    @Override
    public void delete(Film film) {
        films.remove(film.getId());
    }

    @Override
    public List<Film> getAll() {
        log.debug("- getResponse: Текущее количество фильмов: {}", films.size());
        return new ArrayList<>(films.values());
    }

    public int getIdCounter() {
        return idCounter++;
    }
}
