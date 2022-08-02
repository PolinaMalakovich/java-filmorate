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
  private static final String MPAS_TABLE_NAME = "mpas";
  private static final String MPAS_ID_COLUMN = "id";
  private static final String MPAS_NAME_COLUMN = "name";

  // language=sql
  private static final String SELECT_RATING_BY_ID = "SELECT " +
      MPAS_ID_COLUMN + ", " +
      MPAS_NAME_COLUMN +
      " FROM " + MPAS_TABLE_NAME +
      " WHERE " + MPAS_ID_COLUMN + " = ?" + ";";

  // language=sql
  private static final String SELECT_RATINGS = "SELECT * " +
      " FROM " + MPAS_TABLE_NAME + ";";

  private final JdbcTemplate jdbcTemplate;

  public DbMpaStorage(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public Optional<Mpa> addMpa(Mpa mpa) {
    final SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
        .withTableName(MPAS_TABLE_NAME)
        .usingGeneratedKeyColumns(MPAS_ID_COLUMN);
    Map<String, Object> args = toMap(mpa);
    long id = simpleJdbcInsert.executeAndReturnKey(args).longValue();

    return getMpaById(id);
  }

  @Override
  public Optional<Mpa> getMpaById(Long id) {
    return jdbcTemplate
        .query(SELECT_RATING_BY_ID, this::toMpa, id)
        .stream()
        .findFirst();
  }

  @Override
  public Stream<Mpa> getMpas() {
    return jdbcTemplate
        .query(SELECT_RATINGS, this::toMpa)
        .stream();
  }

  private Mpa toMpa(ResultSet resultSet, int rowNumber) throws SQLException {
    return new Mpa(
        resultSet.getLong(MPAS_ID_COLUMN),
        resultSet.getString(MPAS_NAME_COLUMN)
    );
  }

  private Map<String, Object> toMap(Mpa mpa) {
    Map<String, Object> parameters = new HashMap<>();
    parameters.put(MPAS_ID_COLUMN, mpa.getId());
    parameters.put(MPAS_NAME_COLUMN, mpa.getName());

    return parameters;
  }
}
