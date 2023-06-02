package ru.yandex.practicum.filmorate.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;

@Component
@Slf4j
public class FilmValidator {

    public boolean validate(Film film) {
        if (Objects.nonNull(film)) {
            return validateFilmReleaseDate(film);
        } else {
            log.error("Получен пустой запрос");
            throw new ValidationException("Получен пустой запрос");
        }
    }

    private boolean validateFilmReleaseDate(Film film) {
        log.debug("Фильм на валидацию: {}", film);
        LocalDate releaseDate = film.getReleaseDate();
        if (releaseDate.isBefore(LocalDate.of(1895, 12, 28))) {
            log.error("Дата релиза — не может быть раньше 28 декабря 1895 года.");
            throw new ValidationException("Дата релиза — не может быть раньше 28 декабря 1895 года.");
        }

        return true;
    }

    public void validateFilmHasId(Map<Integer, Film> films, Film film) {
        log.debug("Фильм на валидацию: {}", film);
        if (film.getId() != null && films.containsKey(film.getId())) {
            films.replace(film.getId(), film);
        } else {
            log.warn("Не указан Id фильма");
            throw new ValidationException("Не указан Id фильма" + " Укажите и попробуйте снова");
        }

        log.debug("Обновлен фильм с Id: {}", film.getId());
    }

    public boolean validateFilmId(Map<Integer, Film> films, int filmId) {
        log.debug("Фильм на валидацию: {}", filmId);
        if (filmId < 1) {
            throw new ValidationException("Получен запрос с пустым или некорректным filmId");
        }
        if (!films.containsKey(filmId)) {
            log.error("Фильм с {} не найден", filmId);
            throw new ValidationException("Фильм с " + filmId + " + не найден!");
        }

        return true;
    }
}
