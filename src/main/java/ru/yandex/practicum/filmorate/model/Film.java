package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.Optional;

@Data
@Builder
public class Film {
    private final String name;
    private final String description;
    private final Optional<LocalDate> releaseDate;
    private final int duration;
    private Integer id;
}
