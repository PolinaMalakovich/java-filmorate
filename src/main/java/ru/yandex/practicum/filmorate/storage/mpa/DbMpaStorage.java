package ru.yandex.practicum.filmorate.storage.mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;

@Component
public class DbMpaStorage implements MpaStorage {
  private final JdbcTemplate jdbcTemplate;

  public DbMpaStorage(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public Optional<Mpa> addMpa(Mpa mpa) {
    final SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
        .withTableName("mpas")
        .usingGeneratedKeyColumns("id");
    Map<String, Object> args = toMap(mpa);
    long id = simpleJdbcInsert.executeAndReturnKey(args).longValue();

    return getMpaById(id);
  }

  @Override
  public Optional<Mpa> getMpaById(Long id) {
    final String selectRatingById = "SELECT id, name FROM mpas WHERE id = ?;";
    return jdbcTemplate
        .query(selectRatingById, this::toMpa, id)
        .stream()
        .findFirst();
  }

  @Override
  public Stream<Mpa> getMpas() {
    final String selectRatings = "SELECT *  FROM mpas;";
    return jdbcTemplate
        .query(selectRatings, this::toMpa)
        .stream();
  }

  private Mpa toMpa(ResultSet resultSet, int rowNumber) throws SQLException {
    return new Mpa(
        resultSet.getLong("id"),
        resultSet.getString("name")
    );
  }

  private Map<String, Object> toMap(Mpa mpa) {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put("id", mpa.getId());
    parameters.put("name", mpa.getName());

    return parameters;
  }
}
