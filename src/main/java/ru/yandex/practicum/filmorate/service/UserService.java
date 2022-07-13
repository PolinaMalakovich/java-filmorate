package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toSet;

@Service
@Slf4j
public final class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(final UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User addUser(final User newUser) {
        final User user = userStorage.addUser(newUser);
        log.info("New user created successfully");

        return user;
    }

    public User updateUser(final User user) {
        final User updated = userStorage.updateUser(user)
                .orElseThrow(() -> new EntityNotFoundException("User", user.getId()));
        log.info("User " + user.getId() + " updated successfully");

        return updated;
    }

    public User getUserById(final Long id) {
        return userStorage.getUserById(id)
                .orElseThrow(() -> new EntityNotFoundException("User", id));
    }

    public Stream<User> getUsers() {
        return userStorage.getUsers();
    }

    public User addFriend(final Long id, final Long friendId) {
        final User user = addOrDeleteFriendHelper(id, friendId, true);
        final User friend = addOrDeleteFriendHelper(friendId, id, true);
        updateUser(friend);

        return updateUser(user);
    }

    public User deleteFriend(final Long id, final Long friendId) {
        final User user = addOrDeleteFriendHelper(id, friendId, false);
        final User friend = addOrDeleteFriendHelper(friendId, id, false);
        updateUser(friend);

        return updateUser(user);
    }

    private User addOrDeleteFriendHelper(final Long id, final Long friendId, final boolean shouldAdd) {
        final User user = getUserById(id);
        final User friend = getUserById(friendId);
        Set<Long> friends = user.getFriends().collect(toSet());

        if (shouldAdd) {
            friends.add(friend.getId());
        } else {
            friends.remove(friend.getId());
        }

        return user.withFriends(friends);
    }

    public Stream<User> getMutualFriends(final Long id, final Long otherId) {
        final User user = getUserById(id);
        final User other = getUserById(otherId);
        Set<Long> mutualFriends = user.getFriends().collect(Collectors.toSet());
        mutualFriends.retainAll(other.getFriends().collect(toSet()));

        return mutualFriends.stream().map(this::getUserById);
    }

    public Stream<User> getFriends(final Long id) {
        return getUserById(id)
                .getFriends()
                .map(this::getUserById);
    }
}
