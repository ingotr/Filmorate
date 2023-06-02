package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@ResponseBody
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        return filmService.getAll();
    }

    @GetMapping("/{filmId}")
    public Film getById(@PathVariable int filmId) {
        return filmService.getById(filmId);
    }

    @PostMapping
    public Film add(@Valid @RequestBody Film film) {
        log.debug("+ postResponse: {}", film);
        return filmService.add(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.debug("+ putResponse: {}", film);
        return filmService.update(film);
    }

    @DeleteMapping
    public void delete(Film film) {
        log.debug("+ deleteResponse: {}", film);
        filmService.delete(film);
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        if (userId < 0) {
            throw new ValidationException("ID пользователя должен быть больше или равен 0.");
        } else {
            filmService.addLike(id, userId);
        }
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable int id, @PathVariable int userId) {
        if (userId < 0) {
            throw new ValidationException("ID пользователя должен быть больше или равен 0.");
        } else {
            filmService.deleteLike(id, userId);
        }
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(required = false, defaultValue = "10") int count) {
        if (count < 0) {
            throw new IllegalArgumentException("Размер списка должен быть больше или равен 0.");
        }
        return filmService.getPopularFilms(count);
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleValidationException(final ValidationException e) {
        return Map.of("validationError", e.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundException(final NotFoundException e) {
        return Map.of("notFoundError", e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleRuntimeException(final RuntimeException e) {
        log.error("Internal server error: {}", e.getMessage(), e);
        return Map.of("internalError", "Внутренняя ошибка сервера.");
    }
}
