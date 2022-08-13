package ru.yandex.practicum.filmorate.storage.like;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;

@Component
public class DbLikeStorage implements LikeStorage {
  private final JdbcTemplate jdbcTemplate;

  @Autowired
  public DbLikeStorage(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public void addLike(final Long id, final Long userId) {
    final SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
        .withTableName("likes");
    simpleJdbcInsert.execute(toMap(id, userId));
  }

  @Override
  public void deleteLike(final Long id, final Long userId) {
    final String deleteLike = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
    boolean isDeleted = jdbcTemplate.update(deleteLike, id, userId) == 1;
    if (!isDeleted) {
      throw new EntityNotFoundException("Like", userId);
    }
  }

  @Override
  public Stream<Long> getLikes(Long filmId) {
    final String selectLikesByFilmId = "SELECT user_id FROM likes WHERE film_id = ?;";
    return jdbcTemplate
        .queryForList(selectLikesByFilmId, Long.class, filmId)
        .stream();
  }

  private Map<String, Object> toMap(Long filmId, Long userId) {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("film_id", filmId);
    parameters.put("user_id", userId);

    return parameters;
  }
}
