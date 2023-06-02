package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Component
public interface FilmStorage {
    Film add(Film film);

    Film update(Film film);

    void delete(Film film);

    List<Film> getAll();

    Film getById(int filmId);

    void addLike(int id, int userId);

    void deleteLike(int id, int userId);

    List<Film> getPopularFilms(int count);
}
