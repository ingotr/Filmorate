package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Component
public interface UserStorage {
    User add(User user);

    User update(User user);

    void delete(User user);

    List<User> getAll();

    Optional<User> getById(int userId);

    void addFriend(int id, int friendId);

    void deleteFriend(int id, int friendId);

    Set<Long> getFriends(int id);

    Set<Long> getCommonFriends(int id, int otherId);
}
