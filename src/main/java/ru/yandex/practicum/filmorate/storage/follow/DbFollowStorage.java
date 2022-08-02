package ru.yandex.practicum.filmorate.storage.follow;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Follow;
import ru.yandex.practicum.filmorate.model.User;

@Component
public class DbFollowStorage implements FollowStorage {
  private static final String FRIENDS_TABLE_NAME = "friends";
  private static final String FRIENDS_USER_ID_COLUMN = "user_id";
  private static final String FRIENDS_FRIEND_ID_COLUMN = "friend_id";

  // language=sql
  private static final String SELECT_FRIENDS_BY_USER_ID = "SELECT " +
      FRIENDS_FRIEND_ID_COLUMN +
      " FROM " + FRIENDS_TABLE_NAME +
      " WHERE " + FRIENDS_USER_ID_COLUMN + " = ?;";

  // language=sql
  private static final String DELETE_FRIENDS_BY_USER_ID = "DELETE FROM " +
      FRIENDS_TABLE_NAME +
      " WHERE " + FRIENDS_USER_ID_COLUMN + " = ?;";

  private final JdbcTemplate jdbcTemplate;

  public DbFollowStorage(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public Stream<Long> addFollowers(User user) {
    final SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
        .withTableName(FRIENDS_TABLE_NAME);
    user.getFriends().forEach(friendId -> simpleJdbcInsert.execute(toMap(user.getId(), friendId)));

    return getFollowers(user.getId());
  }

  @Override
  public Stream<Long> getFollowers(Long userId) {
    return jdbcTemplate
        .queryForList(SELECT_FRIENDS_BY_USER_ID, Long.class, userId)
        .stream();
  }

  @Override
  public Stream<Long> updateFollowers(User user) {
    deleteFollowers(user.getId());

    return addFollowers(user);
  }

  private Follow toFollow(ResultSet resultSet, int rowNumber) throws SQLException {
    return new Follow(
        resultSet.getLong(FRIENDS_USER_ID_COLUMN),
        resultSet.getLong(FRIENDS_FRIEND_ID_COLUMN)
    );
  }

  private Map<String, Object> toMap(Long userId, Long friendId) {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put(FRIENDS_USER_ID_COLUMN, userId);
    parameters.put(FRIENDS_FRIEND_ID_COLUMN, friendId);

    return parameters;
  }

  private void deleteFollowers(Long userId) {
    jdbcTemplate.update(DELETE_FRIENDS_BY_USER_ID, userId);
  }
}
