package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Optional;
import java.util.stream.Stream;

public interface UserStorage {
    User addUser(User newUser);

    Optional<User> updateUser(User user);

    Optional<User> getUserById(Long id);

    Stream<User> getUsers();
}
