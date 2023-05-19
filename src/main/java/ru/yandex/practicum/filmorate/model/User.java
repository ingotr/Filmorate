package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class User {
    private final String email;
    private final String login;
    private final LocalDate birthday;
    private Integer id;
    private String name;
}
