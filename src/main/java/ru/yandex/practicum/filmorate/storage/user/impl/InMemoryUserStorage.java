package ru.yandex.practicum.filmorate.storage.user.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.validation.UserValidator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private final UserValidator userValidator;
    private int idCounter = 1;

    @Autowired
    public InMemoryUserStorage(UserValidator userValidator) {
        this.userValidator = userValidator;
    }

    public Map<Integer, User> getUsers() {
        return users;
    }

    public UserValidator getUserValidator() {
        return userValidator;
    }

    @Override
    public User add(User user) {
        boolean valid = userValidator.validate(user);
        if (valid) {
            user.setId(getIdCounter());
            user.setFriends(new HashSet<>());
            users.put(user.getId(), user);
            log.debug("- getResponse: {}", user);
        }
        log.debug("- createUser() added user: {}", valid);
        return user;
    }

    @Override
    public User update(User user) {
        log.debug("+ putResponse: {}", user);
        if (!users.containsKey(user.getId())) {
            log.error("Пользователь с userId {} не найден", user.getId());
            throw new NotFoundException("Пользователь не найден.");
        }
        users.replace(user.getId(), user);
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

    public User getById(int userId) {
        if (!users.containsKey(userId)) {
            log.error("Пользователь с userId {} не найден", userId);
            throw new NotFoundException("Пользователь не найден");
        }

        return users.get(userId);
    }

    @Override
    public void addFriend(int id, int friendId) {
        if (!users.containsKey(id)) {
            log.error("Пользователь с id {} не найден", id);
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        if (!users.containsKey(friendId)) {
            log.error("Пользователь с friendId {} не найден", friendId);
            throw new NotFoundException("Пользователь с friendId " + friendId + " не найден");
        }

        User user = users.get(id);
        User friend = users.get(friendId);
        Set<Long> userFriends = user.getFriends();
        Set<Long> friendFriends = friend.getFriends();

        if (userFriends == null) {
            userFriends = new HashSet<>();
            user.setFriends(userFriends);
        }
        if (friendFriends == null) {
            friendFriends = new HashSet<>();
            friend.setFriends(friendFriends);
        }

        userFriends.add((long) friendId);
        friendFriends.add((long) id);

        log.debug("Пользователю id {} добавлен новый друг с Id {}", id, friendId);
    }

    @Override
    public void deleteFriend(int id, int friendId) {
        if (!users.containsKey(id)) {
            log.error("Пользователь с id {} не найден", id);
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        if (!users.containsKey(friendId)) {
            log.error("Пользователь с friendId {} не найден", friendId);
            throw new NotFoundException("Пользователь с friendId " + friendId + " не найден");
        }

        Set<Long> friendsOne = users.get(id).getFriends();
        Set<Long> friendsTwo = users.get(friendId).getFriends();
        if (!friendsOne.isEmpty()) {
            friendsOne.remove((long) friendId);
            friendsTwo.remove((long) id);
            log.debug("У пользователя {} удален друг с Id {}", id, friendId);
            log.debug("У пользователя {} удален друг с Id {}", friendId, id);
        } else {
            log.debug("Список друзей пуст");
        }
    }

    @Override
    public List<User> getFriends(int id) {
        if (!users.containsKey(id)) {
            log.error("Пользователь с id {} не найден", id);
            throw new ValidationException("Пользователь с id " + id + " не найден");
        }

        Set<Long> friends = users.get(id).getFriends();
        if (friends == null || friends.isEmpty()) {
            log.debug("Список друзей пользователя {} пуст", id);
            return new ArrayList<>();
        }

        return friends.stream()
                .filter(friends::contains)
                .map(users::get)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> getCommonFriends(int id, int otherId) {
        if (!users.containsKey(id)) {
            log.error("Пользователь с id {} не найден", id);
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        if (!users.containsKey(otherId)) {
            log.error("Пользователь с otherId {} не найден", otherId);
            throw new NotFoundException("Пользователь с otherId " + otherId + " не найден");
        }
        User currentUser = users.get(id);
        User otherUser = users.get(otherId);

        Set<Long> currentUserFriends = currentUser.getFriends();
        Set<Long> otherUserFriends = otherUser.getFriends();

        if (currentUserFriends.isEmpty()) {
            return new ArrayList<>();
        }

        currentUserFriends.retainAll(otherUserFriends);


        return currentUserFriends.stream()
                .map(users::get)
                .collect(Collectors.toList());
    }

    public int getIdCounter() {
        return idCounter++;
    }
}
