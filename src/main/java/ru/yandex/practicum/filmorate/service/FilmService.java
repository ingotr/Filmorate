package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
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
}