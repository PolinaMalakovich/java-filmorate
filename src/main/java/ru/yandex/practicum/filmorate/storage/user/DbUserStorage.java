package ru.yandex.practicum.filmorate.storage.user;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

@Component
@Qualifier("userDbStorage")
public class DbUserStorage implements UserStorage {
  private final JdbcTemplate jdbcTemplate;

  @Autowired
  public DbUserStorage(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public Optional<User> addUser(User user) {
    final SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
        .withTableName("users")
        .usingGeneratedKeyColumns("id");
    Map<String, Object> args = toMap(user);
    long id = simpleJdbcInsert.executeAndReturnKey(args).longValue();

    return getUserById(id);
  }

  @Override
  public Optional<User> getUserById(Long id) {
    final String selectUserById =
        "SELECT id, email, login, name, birthday FROM users WHERE id = ?;";
    return jdbcTemplate
        .query(selectUserById, this::toUser, id)
        .stream()
        .findFirst();
  }

  @Override
  public Stream<User> getUsers() {
    final String selectUsers = "SELECT *  FROM users;";
    return jdbcTemplate
        .query(selectUsers, this::toUser)
        .stream();
  }

  @Override
  public Stream<User> getFriends(Long id) {
    final String selectFriends = "SELECT users.* " +
        "FROM friends " +
        "LEFT JOIN users ON friends.friend_id = users.id " +
        "WHERE user_id = ? " +
        "ORDER BY users.id;";
    return jdbcTemplate
        .query(selectFriends, this::toUser, id)
        .stream();
  }

  @Override
  public Stream<User> getMutualFriends(Long id, Long otherId) {
    final String selectMutualFriends = "SELECT users.* " +
        "FROM friends " +
        "LEFT JOIN users ON friends.friend_id = users.id " +
        "WHERE user_id IN (?, ?) " +
        "GROUP BY friend_id " +
        "HAVING COUNT(friends.user_id) = 2;";
    return jdbcTemplate
        .query(selectMutualFriends, this::toUser, id, otherId)
        .stream();
  }

  @Override
  public Optional<User> updateUser(User user) {
    final String updateUser = "UPDATE users SET email = ?, login = ?, name = ?, birthday = ? " +
        "WHERE id = ?;";
    jdbcTemplate.update(
        updateUser,
        user.getEmail(),
        user.getLogin(),
        user.getName(),
        user.getBirthday(),
        user.getId()
    );

    return getUserById(user.getId());
  }

  private User toUser(ResultSet resultSet, int rowNumber) throws SQLException {
    return new User(
        resultSet.getLong("id"),
        resultSet.getString("email"),
        resultSet.getString("login"),
        resultSet.getString("name"),
        resultSet.getDate("birthday").toLocalDate(),
        new HashSet<>()
    );
  }

  private Map<String, Object> toMap(User user) {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("id", user.getId());
    parameters.put("email", user.getEmail());
    parameters.put("login", user.getLogin());
    parameters.put("name", user.getName());
    parameters.put("birthday", user.getBirthday());

    return parameters;
  }
}
