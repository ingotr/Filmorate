package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private FilmStorage filmStorage;

    @GetMapping
    public List<Film> getAllFilms() {
        return filmStorage.getAll();
    }

    @PostMapping
    public Film add(@Valid @RequestBody Film film) {
        log.debug("+ postResponse: {}", film);
        return filmStorage.add(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.debug("+ putResponse: {}", film);
        return filmStorage.update(film);
    }

    @DeleteMapping
    public void delete(Film film) {
        log.debug("+ deleteResponse: {}", film);
        filmStorage.delete(film);
    }
}
