package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.stream.Stream;

public interface UserStorage {
    User addUser(User newUser);

    User updateUser(User user);

    User getUserById(Long id);

    Stream<User> getUsers();

    Stream<User> getFriends(Long id);
}
