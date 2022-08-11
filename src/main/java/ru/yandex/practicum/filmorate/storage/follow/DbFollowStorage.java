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

@Component
public class DbFollowStorage implements FollowStorage {
  private final JdbcTemplate jdbcTemplate;

  public DbFollowStorage(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public Stream<Long> addFollowers(Long id, Stream<Long> followers) {
    final SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
        .withTableName("friends");
    followers.forEach(friendId -> simpleJdbcInsert.execute(toMap(id, friendId)));

    return getFollowers(id);
  }

  @Override
  public Stream<Long> getFollowers(Long userId) {
    final String selectFriendsByUserId = "SELECT friend_id FROM friends WHERE user_id = ?;";
    return jdbcTemplate
        .queryForList(selectFriendsByUserId, Long.class, userId)
        .stream();
  }

  @Override
  public Stream<Long> updateFollowers(Long id, Stream<Long> followers) {
    deleteFollowers(id);

    return addFollowers(id, followers);
  }

  private Follow toFollow(ResultSet resultSet, int rowNumber) throws SQLException {
    return new Follow(
        resultSet.getLong("user_id"),
        resultSet.getLong("friend_id")
    );
  }

  private Map<String, Object> toMap(Long userId, Long friendId) {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("user_id", userId);
    parameters.put("friend_id", friendId);

    return parameters;
  }

  private void deleteFollowers(Long userId) {
    final String deleteFriendsByUserId = "DELETE FROM friends WHERE user_id = ?;";
    jdbcTemplate.update(deleteFriendsByUserId, userId);
  }
}
