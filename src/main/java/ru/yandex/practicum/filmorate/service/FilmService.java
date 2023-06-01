package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
@Service
public class FilmService {

    private final FilmStorage filmStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    public void addLike(Film film) {
        Integer likeCount = film.getLikeCount();
        film.setLikeCount(likeCount + 1);
    }

    public void deleteLike(Film film) {
        Integer likeCount = film.getLikeCount();
        if (likeCount > 0) {
            film.setLikeCount(likeCount - 1);
        } else {
            log.debug("Число лайков у фильма равно 0");
        }
    }

    public List<Film> getTopTenFilms(FilmStorage filmStorage) {
        return filmStorage.getAll().stream()
                .filter(x -> x.getLikeCount() >= 10)
                .collect(Collectors.toList());
    }

    public Film add(Film film) {
        return filmStorage.add(film);
    }

    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film update(Film film) {
        return filmStorage.update(film);
    }

    public void delete(Film film) {
        filmStorage.delete(film);
    }
}