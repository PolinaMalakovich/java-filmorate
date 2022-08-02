package ru.yandex.practicum.filmorate.storage.like;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

@Component
public class DbLikeStorage implements LikeStorage {
  public static final String LIKES_TABLE_NAME = "likes";
  public static final String LIKES_FILM_ID_COLUMN = "film_id";
  public static final String LIKES_USER_ID_COLUMN = "user_id";

  // language=sql
  public static final String SELECT_LIKES_BY_FILM_ID = "SELECT " +
      LIKES_USER_ID_COLUMN +
      " FROM " + LIKES_TABLE_NAME +
      " WHERE " + LIKES_FILM_ID_COLUMN + " = ?;";

  // language=sql
  public static final String DELETE_LIKES_BY_FILM_ID = "DELETE FROM " +
      LIKES_TABLE_NAME +
      " WHERE " + LIKES_FILM_ID_COLUMN + " = ?;";

  private final JdbcTemplate jdbcTemplate;

  @Autowired
  public DbLikeStorage(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public Stream<Long> addLikes(Film film) {
    final SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
        .withTableName(LIKES_TABLE_NAME);
    film.getLikes().forEach(userId -> simpleJdbcInsert.execute(toMap(film.getId(), userId)));

    return getLikes(film.getId());
  }

  @Override
  public Stream<Long> getLikes(Long filmId) {
    return jdbcTemplate
        .queryForList(SELECT_LIKES_BY_FILM_ID, Long.class, filmId)
        .stream();
  }

  @Override
  public Stream<Long> updateLikes(Film film) {
    deleteLikes(film.getId());

    return addLikes(film);
  }

  private Map<String, Object> toMap(Long filmId, Long userId) {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put(LIKES_FILM_ID_COLUMN, filmId);
    parameters.put(LIKES_USER_ID_COLUMN, userId);

    return parameters;
  }

  private void deleteLikes(Long filmId) {
    jdbcTemplate.update(DELETE_LIKES_BY_FILM_ID, filmId);
  }
}
