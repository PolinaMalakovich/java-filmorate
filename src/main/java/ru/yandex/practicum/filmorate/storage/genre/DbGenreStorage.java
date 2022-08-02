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
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

@Component
public class DbGenreStorage implements GenreStorage {
  private static final String GENRES_TABLE_NAME = "genres";
  private static final String GENRES_ID_COLUMN = "id";
  private static final String GENRES_NAME_COLUMN = "name";
  private static final String FILM_GENRES_TABLE_NAME = "film_genres";
  private static final String FILM_GENRES_FILM_ID_COLUMN = "film_id";
  private static final String FILM_GENRES_GENRE_ID_COLUMN = "genre_id";

  // language=sql
  private static final String SELECT_GENRE_BY_ID = "SELECT " +
      GENRES_ID_COLUMN + ", " +
      GENRES_NAME_COLUMN +
      " FROM " + GENRES_TABLE_NAME +
      " WHERE " + GENRES_ID_COLUMN + " = ?" + ";";

  // language=sql
  private static final String SELECT_GENRES = "SELECT * " +
      " FROM " + GENRES_TABLE_NAME +
      " ORDER BY " + GENRES_ID_COLUMN + ";";

  // language=sql
  private static final String SELECT_GENRES_BY_FILM_ID = "SELECT " +
      FILM_GENRES_GENRE_ID_COLUMN +
      " FROM " + FILM_GENRES_TABLE_NAME +
      " WHERE " + FILM_GENRES_FILM_ID_COLUMN + " = ?" +
      " ORDER BY " + FILM_GENRES_GENRE_ID_COLUMN + ";";

  // language=sql
  private static final String DELETE_GENRES_BY_FILM_ID = "DELETE FROM " +
      FILM_GENRES_TABLE_NAME +
      " WHERE " + FILM_GENRES_FILM_ID_COLUMN + " = ?;";

  private final JdbcTemplate jdbcTemplate;

  public DbGenreStorage(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public Optional<Genre> addGenre(Genre genre) {
    final SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
        .withTableName(GENRES_TABLE_NAME)
        .usingGeneratedKeyColumns(GENRES_ID_COLUMN);
    Map<String, Object> args = toMap(genre);
    long id = simpleJdbcInsert.executeAndReturnKey(args).longValue();

    return getGenreById(id);
  }

  @Override
  public Stream<Genre> addGenres(Film film) {
    final SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
        .withTableName(FILM_GENRES_TABLE_NAME);
    film.getGenres().forEach(genre -> simpleJdbcInsert.execute(toMap(film.getId(), genre.getId())));

    return getGenresByFilmId(film.getId());
  }

  @Override
  public Optional<Genre> getGenreById(Long id) {
    return jdbcTemplate
        .query(SELECT_GENRE_BY_ID, this::toGenre, id)
        .stream()
        .findFirst();
  }

  @Override
  public Stream<Genre> getGenres() {
    return jdbcTemplate
        .query(SELECT_GENRES, this::toGenre)
        .stream();
  }

  @Override
  public Stream<Genre> getGenresByFilmId(Long filmId) {
    return jdbcTemplate
        .queryForList(SELECT_GENRES_BY_FILM_ID, Long.class, filmId)
        .stream()
        .flatMap(id -> getGenreById(id).stream());
  }

  @Override
  public Stream<Genre> updateGenres(Film film) {
    deleteGenres(film.getId());

    return addGenres(film);
  }

  private Genre toGenre(ResultSet resultSet, int rowNumber) throws SQLException {
    return new Genre(
        resultSet.getLong(GENRES_ID_COLUMN),
        resultSet.getString(GENRES_NAME_COLUMN)
    );
  }

  private Map<String, Object> toMap(Genre genre) {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put(GENRES_NAME_COLUMN, genre.getName());

    return parameters;
  }

  private Map<String, Object> toMap(Long filmId, Long genreId) {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put(FILM_GENRES_FILM_ID_COLUMN, filmId);
    parameters.put(FILM_GENRES_GENRE_ID_COLUMN, genreId);

    return parameters;
  }

  private void deleteGenres(Long filmId) {
    jdbcTemplate.update(DELETE_GENRES_BY_FILM_ID, filmId);
  }
}
