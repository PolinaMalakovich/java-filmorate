package ru.yandex.practicum.filmorate.storage.director;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;

@Component
@Qualifier("DbDirectorStorage")
public class DbDirectorStorage implements DirectorStorage {
  private final JdbcTemplate jdbcTemplate;

  @Autowired
  public DbDirectorStorage(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public Optional<Director> addDirector(Director director) {
    final SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
        .withTableName("directors")
        .usingGeneratedKeyColumns("director_id");
    Map<String, Object> args = toMap(director);
    long id = simpleJdbcInsert.executeAndReturnKey(args).longValue();

    return getDirector(id);
  }

  @Override
  public Optional<Director> getDirector(Long id) {
    final String selectDirector = "SELECT director_id, name FROM directors WHERE director_id = ?;";
    return jdbcTemplate
        .query(selectDirector, this::toDirector, id)
        .stream()
        .findFirst();
  }

  @Override
  public Stream<Director> getDirectors() {
    final String selectDirectors = "SELECT * FROM directors ORDER BY director_id;";
    return jdbcTemplate
        .query(selectDirectors, this::toDirector)
        .stream();
  }

  @Override
  public Optional<Director> updateDirector(Director director) {
    final String updateDirector = "UPDATE directors SET name = ? WHERE director_id = ?;";
    jdbcTemplate.update(
        updateDirector,
        director.getName(),
        director.getId()
    );

    return getDirector(director.getId());
  }

  @Override
  public void deleteDirector(Long id) {
    final String deleteDirector = "DELETE FROM directors WHERE director_id = ?;";
    jdbcTemplate.update(deleteDirector, id);
  }

  private Director toDirector(ResultSet resultSet, int i) throws SQLException {
    return new Director(
        resultSet.getLong("directors.director_id"),
        resultSet.getString("directors.name")
    );
  }

  private Map<String, Object> toMap(Director director) {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("director_id", director.getId());
    parameters.put("name", director.getName());

    return parameters;
  }
}
