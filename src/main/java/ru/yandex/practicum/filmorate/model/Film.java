package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Optional;

@Data
@Builder
public class Film {
    private final Integer id;
    private final String name;
    private final String description;
    private final Optional<LocalDateTime> releaseDate;
    private final int duration;
}
