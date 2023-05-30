package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Set;

@Slf4j
@Service
public class UserService {
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
}
