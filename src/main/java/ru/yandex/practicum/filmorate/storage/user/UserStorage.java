package ru.yandex.practicum.filmorate.storage.user;

import java.util.Optional;
import java.util.stream.Stream;
import ru.yandex.practicum.filmorate.model.User;

public interface UserStorage {
  Optional<User> addUser(User newUser);

  Optional<User> getUserById(Long id);

  Stream<User> getUsers();

  Optional<User> updateUser(User user);
}
