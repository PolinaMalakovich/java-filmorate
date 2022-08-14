package ru.yandex.practicum.filmorate.storage.genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;

@Component
public class DbGenreStorage implements GenreStorage {
  private final JdbcTemplate jdbcTemplate;

  public DbGenreStorage(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public Optional<Genre> addGenre(Genre genre) {
    final SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
        .withTableName("genres")
        .usingGeneratedKeyColumns("id");
    Map<String, Object> args = toMap(genre);
    long id = simpleJdbcInsert.executeAndReturnKey(args).longValue();

    return getGenreById(id);
  }

  @Override
  public Stream<Genre> addGenres(Long id, Stream<Genre> genres) {
    final SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
        .withTableName("film_genres");
    genres.forEach(genre -> simpleJdbcInsert.execute(toMap(id, genre.getId())));

    return getGenresByFilmId(id);
  }

  @Override
  public Optional<Genre> getGenreById(Long id) {
    final String selectGenreById = "SELECT id, name FROM genres WHERE id = ?;";
    return jdbcTemplate
        .query(selectGenreById, this::toGenre, id)
        .stream()
        .findFirst();
  }

  @Override
  public Stream<Genre> getGenres() {
    final String selectGenres = "SELECT *  FROM genres ORDER BY id;";
    return jdbcTemplate
        .query(selectGenres, this::toGenre)
        .stream();
  }

  @Override
  public Stream<Genre> getGenresByFilmId(Long filmId) {
    final String selectGenresByFilmId = "SELECT genre_id FROM film_genres " +
        "WHERE film_id = ? " +
        "ORDER BY genre_id;";
    return jdbcTemplate
        .queryForList(selectGenresByFilmId, Long.class, filmId)
        .stream()
        .flatMap(id -> getGenreById(id).stream());
  }

  @Override
  public Stream<Genre> updateGenres(Long id, Stream<Genre> genres) {
    deleteGenres(id);

    return addGenres(id, genres);
  }

  private Genre toGenre(ResultSet resultSet, int rowNumber) throws SQLException {
    return new Genre(
        resultSet.getLong("id"),
        resultSet.getString("name")
    );
  }

  private Map<String, Object> toMap(Genre genre) {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("name", genre.getName());

    return parameters;
  }

  private Map<String, Object> toMap(Long filmId, Long genreId) {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("film_id", filmId);
    parameters.put("genre_id", genreId);

    return parameters;
  }

  private void deleteGenres(Long filmId) {
    final String deleteGenresByFilmId = "DELETE FROM film_genres WHERE film_id = ?;";
    jdbcTemplate.update(deleteGenresByFilmId, filmId);
  }
}
