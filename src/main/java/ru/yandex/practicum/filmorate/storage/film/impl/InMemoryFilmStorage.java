package ru.yandex.practicum.filmorate.storage.film.impl;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.validation.FilmValidator;

import java.util.HashSet;
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
            film.setLikes(new HashSet<>());
            films.put(film.getId(), film);
            log.debug("- postResponse: {}", film);
        }
        log.debug("- createFilm() add film: {}", valid);

        return film;
    }

    @Override
    public Film update(Film film) {
        if (!films.containsKey(film.getId())) {
            log.error("Фильм с iD {} не найден", film.getId());
            throw new NotFoundException("Фильм не найден.");
        }
        films.replace(film.getId(), film);
        log.debug("- putResponse: {}", film);

        return film;
    }

    @Override
    public void delete(Film film) {
        filmValidator.validateFilmHasId(films, film);
        films.remove(film.getId());
    }

    @Override
    public List<Film> getAll() {
        log.debug("- getResponse: Текущее количество фильмов: {}", films.size());
        return new ArrayList<>(films.values());
    }

    public Film getById(int filmId) {
        if (!films.containsKey(filmId)) {
            log.error("Фильм {} не найден", filmId);
            throw new NotFoundException("Фильм с " + filmId + " + не найден!");
        }

        return films.get(filmId);
    }

    @Override
    public void addLike(int id, int userId) {
        if (films == null) {
            log.error("Список фильмов пуст");
            throw new ValidationException("Список фильмов пуст");
        }

        Optional<Film> filmOptional = Optional.ofNullable(films.get(id));
        if (!filmOptional.isPresent()) {
            log.error("Фильм с id {} не найден", id);
            throw new ValidationException("Фильм с id " + id + " не найден");
        }

        Optional.of(userId)
                .filter(x -> x > 0)
                .orElseThrow(() -> {
                    log.error("Неверный id пользователя: {}", userId);
                    return new ValidationException("Неверный id пользователя: " + userId);
                });

        if (!filmOptional.get().getLikes().contains((long) userId)) {
            filmOptional.get().getLikes().add((long) userId);
            log.debug("Пользователь с userId:{} добавил лайк фильму с id:{}", userId, id);
        } else {
            log.debug("Пользователь с userId:{} уже добавил лайк этому фильму", userId);
        }
    }

    @Override
    public void deleteLike(int id, int userId) {
        if (userId < 0) {
            log.error("Пользователь с userId {} не найден", userId);
            throw new NotFoundException("Получен запрос с пустым id пользователя");
        }

        if (!films.get(id).getLikes().isEmpty()) {
            films.get(id).getLikes().remove((long) userId);
            log.debug("Пользователь с userId:{} удалил лайк фильму с id:{}", userId, id);
        } else {
            log.debug("Список лайков фильма id{} пуст!", id);
        }
    }

    @Override
    public List<Film> getPopularFilms(int count) {
        if (films.isEmpty() || films == null) {
            log.error("Список фильмов пуст");
            throw new NotFoundException("Список фильмов пуст");
        }

        if (count < 0) {
            log.error("Некорректное значение параметра count: {}", count);
            throw new ValidationException("Некорректное значение параметра count: " + count);
        }

        return films.values().stream()
                .sorted(Film::compareTo)
                .limit(count)
                .collect(Collectors.toList());
    }

    public int getIdCounter() {
        return idCounter++;
    }
}
