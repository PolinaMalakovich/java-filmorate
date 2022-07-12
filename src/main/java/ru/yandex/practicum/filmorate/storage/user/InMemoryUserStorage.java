package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Stream;

@Component
public class InMemoryUserStorage implements UserStorage {
    private long id = 1;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User addUser(final User newUser) {
        final User user = newUser.withId(id++);
        users.put(user.getId(), user);

        return user;
    }

    @Override
    public User updateUser(final User user) {
        checkIfIdExists(user.getId());
        users.replace(user.getId(), user);
        return user;
    }

    @Override
    public User getUserById(final Long id) {
        checkIfIdExists(id);
        return users.get(id);
    }

    private void checkIfIdExists(final Long id) {
        if (!users.containsKey(id)) throw new EntityNotFoundException("User", id);
    }

    @Override
    public Stream<User> getUsers() {
        return users.values().stream();
    }

    @Override
    public Stream<User> getFriends(final Long id) {
        checkIfIdExists(id);
        return getUserById(id)
                .getFriends()
                .map(this::getUserById);
    }
}
