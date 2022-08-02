package ru.yandex.practicum.filmorate.storage.film;

import static java.util.stream.Collectors.toSet;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Duration;
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
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

@Component
@Qualifier("DbFilmStorage")
public class DbFilmStorage implements FilmStorage {
  private static final String FILMS_TABLE_NAME = "films";
  private static final String FILMS_ID_COLUMN = "id";
  private static final String FILMS_NAME_COLUMN = "name";
  private static final String FILMS_DESCRIPTION_COLUMN = "description";
  private static final String FILMS_RELEASE_DATE_COLUMN = "release_date";
  private static final String FILMS_DURATION_COLUMN = "duration";
  private static final String FILMS_MPA_COLUMN = "mpa";

  // language=sql
  private static final String SELECT_FILM_BY_ID = "SELECT " +
      FILMS_ID_COLUMN + ", " +
      FILMS_NAME_COLUMN + ", " +
      FILMS_DESCRIPTION_COLUMN + ", " +
      FILMS_RELEASE_DATE_COLUMN + ", " +
      FILMS_DURATION_COLUMN + ", " +
      FILMS_MPA_COLUMN +
      " FROM " + FILMS_TABLE_NAME +
      " WHERE " + FILMS_ID_COLUMN + " = ?" + ";";

  // language=sql
  private static final String SELECT_FILMS = "SELECT * " +
      " FROM " + FILMS_TABLE_NAME + ";";

  // language=sql
  private static final String UPDATE_FILM = "UPDATE " + FILMS_TABLE_NAME + " SET " +
      FILMS_NAME_COLUMN + " = ?, " +
      FILMS_DESCRIPTION_COLUMN + " = ?, " +
      FILMS_RELEASE_DATE_COLUMN + " = ?, " +
      FILMS_DURATION_COLUMN + " = ?, " +
      FILMS_MPA_COLUMN + " = ? " +
      " WHERE " + FILMS_ID_COLUMN + " = ?" + ";";

  private final JdbcTemplate jdbcTemplate;
  private final LikeStorage likeStorage;
  private final GenreStorage genreStorage;
  private final MpaStorage mpaStorage;

  @Autowired
  public DbFilmStorage(
      JdbcTemplate jdbcTemplate,
      LikeStorage likeStorage,
      GenreStorage genreStorage,
      MpaStorage mpaStorage) {
    this.jdbcTemplate = jdbcTemplate;
    this.likeStorage = likeStorage;
    this.genreStorage = genreStorage;
    this.mpaStorage = mpaStorage;
  }

  @Override
  public Optional<Film> addFilm(Film film) {
    final SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
        .withTableName(FILMS_TABLE_NAME)
        .usingGeneratedKeyColumns(FILMS_ID_COLUMN);
    Map<String, Object> args = toMap(film);
    long id = simpleJdbcInsert.executeAndReturnKey(args).longValue();
    Film withId = film.withId(id);
    likeStorage.addLikes(withId);
    genreStorage.addGenres(withId);

    return getFilmById(id);
  }

  @Override
  public Optional<Film> getFilmById(Long id) {
    return jdbcTemplate
        .query(SELECT_FILM_BY_ID, this::toFilm, id)
        .stream()
        .findFirst()
        .flatMap(film -> mpaStorage.getMpaById(film.getMpa().getId()).map(film::withMpa))
        .map(film -> film
            .withLikes(likeStorage.getLikes(id).collect(toSet()))
            .withGenres(genreStorage.getGenresByFilmId(id).collect(toSet()))
        );
  }

  @Override
  public Stream<Film> getFilms() {
    return jdbcTemplate
        .query(SELECT_FILMS, this::toFilm)
        .stream()
        .flatMap(film -> mpaStorage.getMpaById(film.getMpa().getId()).map(film::withMpa).stream())
        .map(film -> film
            .withLikes(likeStorage.getLikes(film.getId()).collect(toSet()))
            .withGenres(genreStorage.getGenresByFilmId(film.getId()).collect(toSet()))
        );
  }

  @Override
  public Optional<Film> updateFilm(Film film) {
    jdbcTemplate.update(
        UPDATE_FILM,
        film.getName(),
        film.getDescription(),
        film.getReleaseDate(),
        film.getDuration(),
        film.getMpa().getId(),
        film.getId()
    );
    likeStorage.updateLikes(film);
    genreStorage.updateGenres(film);

    return getFilmById(film.getId());
  }

  private Film toFilm(ResultSet resultSet, int rowNumber) throws SQLException {
    return new Film(
        resultSet.getLong(FILMS_ID_COLUMN),
        new HashSet<>(),
        resultSet.getString(FILMS_NAME_COLUMN),
        resultSet.getString(FILMS_DESCRIPTION_COLUMN),
        resultSet.getDate(FILMS_RELEASE_DATE_COLUMN).toLocalDate(),
        Duration.ofSeconds(resultSet.getInt(FILMS_DURATION_COLUMN)),
        new HashSet<>(),
        new Mpa(resultSet.getLong(FILMS_MPA_COLUMN), "")
    );
  }

  private Map<String, Object> toMap(Film film) {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put(FILMS_ID_COLUMN, film.getId());
    parameters.put(FILMS_NAME_COLUMN, film.getName());
    parameters.put(FILMS_DESCRIPTION_COLUMN, film.getDescription());
    parameters.put(FILMS_RELEASE_DATE_COLUMN, film.getReleaseDate());
    parameters.put(FILMS_DURATION_COLUMN, film.getDuration().getSeconds());
    parameters.put(FILMS_MPA_COLUMN, film.getMpa().getId());

    return parameters;
  }
}
