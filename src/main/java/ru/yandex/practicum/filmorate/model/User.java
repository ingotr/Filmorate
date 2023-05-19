package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class User {
    private final Integer id;
    private final String email;
    private final String login;
    private final LocalDateTime birthday;
    private String name;
}
