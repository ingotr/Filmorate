package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Set;

@Component
@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addFriend(User user) {
        user.getFriends().add(Long.valueOf(user.getId()));
        return user;
    }

    public void deleteFriend(User user) {
        if (user.getFriends().size() > 0) {
            user.getFriends().remove(user.getId());
        } else {
            log.debug("Список друзей пуст");
        }
    }

    public Set<Long> getFriendsId(User user) {
        return user.getFriends();
    }

    public User add(User user) {
        return userStorage.add(user);
    }

    public List<User> getAll() {
        return userStorage.getAll();
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public void delete(User user) {
        userStorage.delete(user);
    }
}
