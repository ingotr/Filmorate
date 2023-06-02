package ru.yandex.practicum.filmorate.storage.user.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validation.UserValidator;

import java.util.*;

@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private final UserValidator userValidator;
    private int idCounter = 1;

    @Autowired
    public InMemoryUserStorage(UserValidator userValidator) {
        this.userValidator = userValidator;
    }

    @Override
    public User add(User user) {
        boolean valid = userValidator.validate(user);
        if (valid) {
            user.setId(getIdCounter());
            users.put(user.getId(), user);
            log.debug("- getResponse: {}", user);
        }
        log.debug("- createUser() added user: {}", valid);
        return user;
    }

    @Override
    public User update(User user) {
        log.debug("+ putResponse: {}", user);
        userValidator.validateUserHasId(users, user);
        log.debug("- putResponse: {}", user);

        return user;
    }

    @Override
    public void delete(User user) {
        users.remove(user.getId());
    }

    @Override
    public List<User> getAll() {
        log.debug("- getResponse: Текущее количество пользователей: {}", users.size());
        return new ArrayList<>(users.values());
    }

    public Optional<User> getById(int userId) {
        if (!userValidator.validateUserId(users, userId)) {
            log.error("Передан несуществующий userId {}", userId);
        }

        return users.values().stream()
                .filter(x -> x.getId() == userId)
                .findFirst();
    }

    @Override
    public void addFriend(int id, int friendId) {
        if (!userValidator.validateUserId(users, id)) {
            log.error("Передан несуществующий id {}", id);
        }
        if (!userValidator.validateUserId(users, friendId)) {
            log.error("Передан несуществующий friendId {}", friendId);
        }

        users.get(id).getFriends().add((long) friendId);
        users.get(friendId).getFriends().add((long) id);
        log.debug("Пользователю id {} добавлен новый друг с Id {}", id, friendId);
    }

    @Override
    public void deleteFriend(int id, int friendId) {
        if (!userValidator.validateUserId(users, id)) {
            log.error("Передан несуществующий id {}", id);
        }
        if (!userValidator.validateUserId(users, friendId)) {
            log.error("Передан несуществующий friendId {}", friendId);
        }

        Set<Long> friends = users.get(id).getFriends();
        if (!friends.isEmpty()) {
            friends.remove((long) friendId);
            log.debug("У пользователя {} удален друг с Id {}", id, friendId);
        } else {
            log.debug("Список друзей пуст");
        }
    }

    @Override
    public Set<Long> getFriends(int id) {
        if (!userValidator.validateUserId(users, id)) {
            log.error("Передан несуществующий id {}", id);
        }
        if (userValidator.validateUserFriendList(users, id)) {
            log.debug("Список друзей пуст");
        }

        return users.get(id).getFriends();
    }

    @Override
    public Set<Long> getCommonFriends(int id, int otherId) {
        if (!userValidator.validateUserId(users, id)) {
            log.error("Передан несуществующий id {}", id);
        }
        if (!userValidator.validateUserId(users, otherId)) {
            log.error("Передан несуществующий otherId {}", otherId);
        }

        Set<Long> currentUserFriends = users.get(id).getFriends();
        Set<Long> otherUserFriends = users.get(otherId).getFriends();
        currentUserFriends.retainAll(otherUserFriends);

        return currentUserFriends;
    }

    public int getIdCounter() {
        return idCounter++;
    }
}
