package ru.yandex.practicum.filmorate.validation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Map;
import java.util.Objects;

@Component
@Slf4j
public class UserValidator {
    public boolean validate(User user) {
        if (Objects.nonNull(user)) {
            validateUserName(user);

            return true;
        } else {
            log.error("Получен пустой запрос");
            throw new ValidationException("Получен пустой запрос");
        }
    }

    public void validateUserName(User user) {
        log.debug("Пользователь на валидацию: {}", user);
        if (user.getName() == null || user.getName().isBlank()) {
            log.warn("Новому пользователю передано пустое имя. Пустое имя будет заменено логином");
            user.setName(user.getLogin());
        }
    }

    public void validateUserHasId(Map<Integer, User> users, User user) {
        log.debug("Пользователь на валидацию: {}", user);
        if (user.getId() != null && users.containsKey(user.getId())) {
            users.replace(user.getId(), user);
        } else {
            log.error("Не указан Id пользователя");
            throw new ValidationException("Не указан Id пользователя" + "Укажите и попробуйте снова");
        }

        log.debug("Обновлен пользователь с Id: {}", user.getId());
    }
}
