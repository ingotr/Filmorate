package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;

@Data
@AllArgsConstructor
public class Film implements Comparable<Film> {
    @NotBlank(message = "Не указано название фильма")
    private final String name;
    @Size(max = 200, message = "Максимальная длина описания - 200 символов")
    private final String description;
    private final LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительной")
    private final Integer duration;
    private Integer id;
    private Set<Long> likes;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Film film = (Film) o;
        return Objects.equals(likes.size(), film.likes.size());
    }

    @Override
    public int hashCode() {
        return Objects.hash(likes);
    }

    @Override
    public int compareTo(Film o) {
        return 0;
    }
}
