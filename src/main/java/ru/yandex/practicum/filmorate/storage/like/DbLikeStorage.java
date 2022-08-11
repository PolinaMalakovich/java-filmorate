package ru.yandex.practicum.filmorate.storage.like;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

@Component
public class DbLikeStorage implements LikeStorage {
  private final JdbcTemplate jdbcTemplate;

  @Autowired
  public DbLikeStorage(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public Stream<Long> addLikes(Long id, Stream<Long> likes) {
    final SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
        .withTableName("likes");
    likes.forEach(userId -> simpleJdbcInsert.execute(toMap(id, userId)));

    return getLikes(id);
  }

  @Override
  public Stream<Long> getLikes(Long filmId) {
    final String selectLikesByFilmId = "SELECT user_id FROM likes WHERE film_id = ?;";
    return jdbcTemplate
        .queryForList(selectLikesByFilmId, Long.class, filmId)
        .stream();
  }

  @Override
  public Stream<Long> updateLikes(Long id, Stream<Long> likes) {
    deleteLikes(id);

    return addLikes(id, likes);
  }

  private Map<String, Object> toMap(Long filmId, Long userId) {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("film_id", filmId);
    parameters.put("user_id", userId);

    return parameters;
  }

  private void deleteLikes(Long filmId) {
    final String deleteLikesByFilmId = "DELETE FROM likes WHERE film_id = ?;";
    jdbcTemplate.update(deleteLikesByFilmId, filmId);
  }
}
