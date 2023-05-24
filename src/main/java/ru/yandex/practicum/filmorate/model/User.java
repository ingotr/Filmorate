package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class User {
    @Email(message = "Неправильный формат почты")
    private final String email;
    @NotBlank(message = "Логин не может быть пустым")
    private final String login;
    @Past(message = "Дата рождения не может быть в будущем")
    private final LocalDate birthday;
    private Integer id;
    private String name;
}
