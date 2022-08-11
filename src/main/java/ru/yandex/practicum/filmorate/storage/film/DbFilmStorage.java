package ru.yandex.practicum.filmorate.storage.film;

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

@Component
@Qualifier("DbFilmStorage")
public class DbFilmStorage implements FilmStorage {
  private final JdbcTemplate jdbcTemplate;

  @Autowired
  public DbFilmStorage(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public Optional<Film> addFilm(Film film) {
    final SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
        .withTableName("films")
        .usingGeneratedKeyColumns("id");
    Map<String, Object> args = toMap(film);
    long id = simpleJdbcInsert.executeAndReturnKey(args).longValue();

    return getFilmById(id);
  }

  @Override
  public Optional<Film> getFilmById(Long id) {
    final String selectFilmById =
        "SELECT films.id, films.name, description, release_date, duration, " +
            "mpas.id AS mpa_id, mpas.name AS mpa_name " +
            "FROM films LEFT JOIN mpas ON films.mpa = mpas.id WHERE films.id = ?;";

    return jdbcTemplate
        .query(selectFilmById, this::toFilm, id)
        .stream()
        .findFirst();
  }

  @Override
  public Stream<Film> getFilms() {
    final String selectFilms =
        "SELECT films.id, films.name, description, release_date, duration, " +
            "mpas.id AS mpa_id, mpas.name AS mpa_name " +
            "FROM films LEFT JOIN mpas ON films.mpa = mpas.id;";
    return jdbcTemplate
        .query(selectFilms, this::toFilm)
        .stream();
  }

  @Override
  public Optional<Film> updateFilm(Film film) {
    final String updateFilm = "UPDATE films " +
        "SET name = ?, description = ?, release_date = ?, duration = ?, mpa = ? " +
        "WHERE id = ?;";
    jdbcTemplate.update(
        updateFilm,
        film.getName(),
        film.getDescription(),
        film.getReleaseDate(),
        film.getDuration(),
        film.getMpa().getId(),
        film.getId()
    );

    return getFilmById(film.getId());
  }

  private Film toFilm(ResultSet resultSet, int rowNumber) throws SQLException {
    return new Film(
        resultSet.getLong("id"),
        new HashSet<>(),
        resultSet.getString("name"),
        resultSet.getString("description"),
        resultSet.getDate("release_date").toLocalDate(),
        Duration.ofSeconds(resultSet.getInt("duration")),
        new HashSet<>(),
        new Mpa(resultSet.getLong("mpa_id"), resultSet.getString("mpa_name"))
    );
  }

  private Map<String, Object> toMap(Film film) {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("id", film.getId());
    parameters.put("name", film.getName());
    parameters.put("description", film.getDescription());
    parameters.put("release_date", film.getReleaseDate());
    parameters.put("duration", film.getDuration().getSeconds());
    parameters.put("mpa", film.getMpa().getId());

    return parameters;
  }
}
