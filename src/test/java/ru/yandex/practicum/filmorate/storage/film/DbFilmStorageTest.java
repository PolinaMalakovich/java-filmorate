package ru.yandex.practicum.filmorate.storage.film;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class DbFilmStorageTest {
  private final DbFilmStorage dbFilmStorage;

  @Test
  @Sql(value = {"add-film.after.sql"}, executionPhase = AFTER_TEST_METHOD)
  void addFilm() {
    Film trainspotting = new Film(
        1L,
        new HashSet<>(),
        "Trainspotting",
        "Absolutely amazing film!",
        LocalDate.of(1996, 2, 23),
        Duration.ofMinutes(93),
        new HashSet<>(),
        new Mpa(4L, "R")
    );
    Optional<Film> filmOptional = dbFilmStorage.addFilm(trainspotting);

    assertThat(filmOptional)
        .isPresent()
        .hasValueSatisfying(film ->
            assertThat(film)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "Trainspotting")
                .hasFieldOrPropertyWithValue("description", "Absolutely amazing film!")
                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(1996, 2, 23))
                .hasFieldOrPropertyWithValue("duration", Duration.ofMinutes(93))
                .hasFieldOrPropertyWithValue("mpa", new Mpa(4L, "R"))
        );
  }

  @Test
  @SqlGroup({
      @Sql(value = {"add-film-when-film-exists.before.sql"}, executionPhase = BEFORE_TEST_METHOD),
      @Sql(value = {"add-film-when-film-exists.after.sql"}, executionPhase = AFTER_TEST_METHOD)
  })
  void addFilmWhenFilmExists() {
    Film trainspotting = new Film(
        1L,
        new HashSet<>(),
        "Trainspotting",
        "Absolutely amazing film!",
        LocalDate.of(1996, 2, 23),
        Duration.ofMinutes(93),
        new HashSet<>(),
        new Mpa(4L, "R")
    );

    assertThatThrownBy(() -> dbFilmStorage.addFilm(trainspotting))
        .isInstanceOf(DuplicateKeyException.class);
  }

  @Test
  @SqlGroup({
      @Sql(value = {"get-film-by-id.before.sql"}, executionPhase = BEFORE_TEST_METHOD),
      @Sql(value = {"get-film-by-id.after.sql"}, executionPhase = AFTER_TEST_METHOD)
  })
  void getFilmById() {
    Optional<Film> filmOptional = dbFilmStorage.getFilmById(1L);

    assertThat(filmOptional)
        .isPresent()
        .hasValueSatisfying(film ->
            assertThat(film)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "Trainspotting")
                .hasFieldOrPropertyWithValue("description", "Absolutely amazing film!")
                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(1996, 2, 23))
                .hasFieldOrPropertyWithValue("duration", Duration.ofMinutes(93))
                .hasFieldOrPropertyWithValue("mpa", new Mpa(4L, "R"))
        );
  }

  @Test
  void getFilmByIdWhenIdDoesNotExist() {
    Optional<Film> filmOptional = dbFilmStorage.getFilmById(2L);

    assertThat(filmOptional).isNotPresent();
  }

  @Test
  @SqlGroup({
      @Sql(value = {"get-films.before.sql"}, executionPhase = BEFORE_TEST_METHOD),
      @Sql(value = {"get-films.after.sql"}, executionPhase = AFTER_TEST_METHOD)
  })
  void getFilms() {
    Stream<Film> filmStream = dbFilmStorage.getFilms();

    assertThat(filmStream).isNotEmpty().hasSameElementsAs(List.of(
        new Film(
            1L,
            new HashSet<>(),
            "Trainspotting",
            "Absolutely amazing film!",
            LocalDate.of(1996, 2, 23),
            Duration.ofMinutes(93),
            new HashSet<>(),
            new Mpa(4L, "R")
        ),
        new Film(
            2L,
            new HashSet<>(),
            "Big Fish",
            "A brilliant experience",
            LocalDate.of(2003, 12, 25),
            Duration.ofMinutes(125),
            new HashSet<>(),
            new Mpa(2L, "PG")
        ),
        new Film(
            3L,
            new HashSet<>(),
            "Doctor Sleep",
            "Wow. Absolutely incredible film.",
            LocalDate.of(2019, 10, 31),
            Duration.ofMinutes(152),
            new HashSet<>(),
            new Mpa(4L, "R")
        )
    ));
  }

  @Test
  void getFilmsWhenFilmsDoNotExist() {
    Stream<Film> filmStream = dbFilmStorage.getFilms();

    assertThat(filmStream).isEmpty();
  }

  @Test
  @SqlGroup({
      @Sql(value = {"update-film.before.sql"}, executionPhase = BEFORE_TEST_METHOD),
      @Sql(value = {"update-film.after.sql"}, executionPhase = AFTER_TEST_METHOD)
  })
  void updateFilm() {
    Film trainspotting = new Film(
        1L,
        new HashSet<>(),
        "Trainspotting",
        "Absolutely amazing film!",
        LocalDate.of(1996, 2, 23),
        Duration.ofMinutes(93),
        new HashSet<>(),
        new Mpa(4L, "R")
    );
    Optional<Film> filmOptional = dbFilmStorage.updateFilm(trainspotting);

    assertThat(filmOptional)
        .isPresent()
        .hasValueSatisfying(film ->
            assertThat(film)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", "Trainspotting")
                .hasFieldOrPropertyWithValue("description", "Absolutely amazing film!")
                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(1996, 2, 23))
                .hasFieldOrPropertyWithValue("duration", Duration.ofMinutes(93))
                .hasFieldOrPropertyWithValue("mpa", new Mpa(4L, "R"))
        );
  }

  @Test
  void updateFilmWhenFilmDoesNotExist() {
    Film film = new Film(
        1L,
        new HashSet<>(),
        "Trainspotting",
        "Absolutely amazing film!",
        LocalDate.of(1996, 2, 23),
        Duration.ofMinutes(93),
        new HashSet<>(),
        new Mpa(4L, "R")
    );
    Optional<Film> filmOptional = dbFilmStorage.updateFilm(film);

    assertThat(filmOptional).isEmpty();
  }
}