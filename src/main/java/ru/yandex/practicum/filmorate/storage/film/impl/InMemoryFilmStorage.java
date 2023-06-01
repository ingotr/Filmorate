package ru.yandex.practicum.filmorate.storage.film.impl;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.validation.FilmValidator;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Slf4j
@Data
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();
    private FilmValidator filmValidator;
    private int idCounter = 1;

    @Autowired
    public InMemoryFilmStorage(FilmValidator filmValidator) {
        this.filmValidator = filmValidator;
    }

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

    public Optional<Film> getById(int filmId) {
        return films.values().stream()
                .filter(x -> x.getId() == filmId)
                .findFirst();
    }

    @Override
    public void addLike(int id, int userId) {
        if (!films.get(id).getLikes().contains((long) userId)) {
            films.get(id).getLikes().add((long) userId);
            log.debug("Пользователь с userId:{} добавил лайк фильму с id:{}", userId, id);
        } else {
            log.debug("Пользователь с userId:{} уже добавил лайк этому фильму", userId);
        }
    }

    @Override
    public void deleteLike(int id, int userId) {
        if (!films.get(id).getLikes().isEmpty()) {
            films.get(id).getLikes().remove((long) userId);
            log.debug("Пользователь с userId:{} удалил лайк фильму с id:{}", userId, id);
        } else {
            log.debug("Список лайков фильма id{} пуст!", id);
        }
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        return films.values().stream()
                .sorted(Film::compareTo)
                .collect(Collectors.toList());
    }

    public int getIdCounter() {
        return idCounter++;
    }
}
