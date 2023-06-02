package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Component
public interface UserStorage {
    User add(User user);

    User update(User user);

    void delete(User user);

    List<User> getAll();

    User getById(int userId);

    void addFriend(int id, int friendId);

    void deleteFriend(int id, int friendId);

    List<User> getFriends(int id);

    List<User> getCommonFriends(int id, int otherId);
}
