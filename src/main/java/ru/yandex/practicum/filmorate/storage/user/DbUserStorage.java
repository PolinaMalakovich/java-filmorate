package ru.yandex.practicum.filmorate.storage.user;

import static java.util.stream.Collectors.toSet;

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
import ru.yandex.practicum.filmorate.storage.follow.FollowStorage;

@Component
@Qualifier("userDbStorage")
public class DbUserStorage implements UserStorage {
  private static final String USERS_TABLE_NAME = "users";
  private static final String USERS_ID_COLUMN = "id";
  private static final String USERS_EMAIL_COLUMN = "email";
  private static final String USERS_LOGIN_COLUMN = "login";
  private static final String USERS_NAME_COLUMN = "name";
  private static final String USERS_BIRTHDAY_COLUMN = "birthday";

  // language=sql
  private static final String SELECT_USER_BY_ID = "SELECT " +
      USERS_ID_COLUMN + ", " +
      USERS_EMAIL_COLUMN + ", " +
      USERS_LOGIN_COLUMN + ", " +
      USERS_NAME_COLUMN + ", " +
      USERS_BIRTHDAY_COLUMN +
      " FROM " + USERS_TABLE_NAME +
      " WHERE " + USERS_ID_COLUMN + " = ?" + ";";

  // language=sql
  private static final String SELECT_USERS = "SELECT * " +
      " FROM " + USERS_TABLE_NAME + ";";

  // language=sql
  private static final String UPDATE_USER = "UPDATE " + USERS_TABLE_NAME + " SET " +
      USERS_EMAIL_COLUMN + " = ?, " +
      USERS_LOGIN_COLUMN + " = ?, " +
      USERS_NAME_COLUMN + " = ?, " +
      USERS_BIRTHDAY_COLUMN + " = ? " +
      " WHERE " + USERS_ID_COLUMN + " = ?" + ";";

  private final JdbcTemplate jdbcTemplate;
  private final FollowStorage followStorage;

  @Autowired
  public DbUserStorage(JdbcTemplate jdbcTemplate, FollowStorage followStorage) {
    this.jdbcTemplate = jdbcTemplate;
    this.followStorage = followStorage;
  }

  @Override
  public Optional<User> addUser(User user) {
    final SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
        .withTableName(USERS_TABLE_NAME)
        .usingGeneratedKeyColumns(USERS_ID_COLUMN);
    Map<String, Object> args = toMap(user);
    long id = simpleJdbcInsert.executeAndReturnKey(args).longValue();
    followStorage.addFollowers(user.withId(id));

    return getUserById(id);
  }

  @Override
  public Optional<User> getUserById(Long id) {
    return jdbcTemplate
        .query(SELECT_USER_BY_ID, this::toUser, id)
        .stream()
        .findFirst()
        .map(user -> user.withFriends(followStorage.getFollowers(id).collect(toSet())));
  }

  @Override
  public Stream<User> getUsers() {
    return jdbcTemplate
        .query(SELECT_USERS, this::toUser)
        .stream()
        .map(user -> user.withFriends(followStorage.getFollowers(user.getId()).collect(toSet())));
  }

  @Override
  public Optional<User> updateUser(User user) {
    jdbcTemplate.update(
        UPDATE_USER,
        user.getEmail(),
        user.getLogin(),
        user.getName(),
        user.getBirthday(),
        user.getId()
    );
    followStorage.updateFollowers(user);

    return getUserById(user.getId());
  }

  private User toUser(ResultSet resultSet, int rowNumber) throws SQLException {
    return new User(
        resultSet.getLong(USERS_ID_COLUMN),
        resultSet.getString(USERS_EMAIL_COLUMN),
        resultSet.getString(USERS_LOGIN_COLUMN),
        resultSet.getString(USERS_NAME_COLUMN),
        resultSet.getDate(USERS_BIRTHDAY_COLUMN).toLocalDate(),
        new HashSet<>()
    );
  }

  private Map<String, Object> toMap(User user) {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put(USERS_ID_COLUMN, user.getId());
    parameters.put(USERS_EMAIL_COLUMN, user.getEmail());
    parameters.put(USERS_LOGIN_COLUMN, user.getLogin());
    parameters.put(USERS_NAME_COLUMN, user.getName());
    parameters.put(USERS_BIRTHDAY_COLUMN, user.getBirthday());

    return parameters;
  }
}
