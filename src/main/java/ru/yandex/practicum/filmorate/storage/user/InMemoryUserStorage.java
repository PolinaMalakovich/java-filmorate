package ru.yandex.practicum.filmorate.storage.user;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

@Component
public class InMemoryUserStorage implements UserStorage {
  private final Map<Long, User> users = new HashMap<>();
  private long id = 1;

  @Override
  public User addUser(final User newUser) {
    final User user = newUser.withId(id++);
    users.put(user.getId(), user);

    return user;
  }

  @Override
  public Optional<User> updateUser(final User user) {
    return getUserById(user.getId())
        .map(f -> {
          users.replace(user.getId(), user);

          return user;
        });
  }

  @Override
  public Optional<User> getUserById(final Long id) {
    return Optional.ofNullable(users.get(id));
  }

  @Override
  public Stream<User> getUsers() {
    return users.values().stream();
  }
}
