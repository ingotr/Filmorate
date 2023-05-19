package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import java.time.LocalDate;

@Data
@Builder
public class User {
    @Email
    private final String email;
    @NotBlank
    private final String login;
    private final LocalDate birthday;
    private Integer id;
    private String name;
}
