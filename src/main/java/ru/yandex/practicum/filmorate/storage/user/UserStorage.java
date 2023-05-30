package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

public interface UserStorage {
    User add(User user);

    User update(int id);

    void delete(int id);
}
