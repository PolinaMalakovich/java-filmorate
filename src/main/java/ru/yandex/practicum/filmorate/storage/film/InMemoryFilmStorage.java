package ru.yandex.practicum.filmorate.storage.film;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

@Component
public final class InMemoryFilmStorage implements FilmStorage {
  private final Map<Long, Film> films = new HashMap<>();
  private long id = 1;

  @Override
  public Optional<Film> addFilm(final Film newFilm) {
    final Film film = newFilm.withId(id++);
    films.put(film.getId(), film);

    return Optional.of(film);
  }

  @Override
  public Optional<Film> updateFilm(final Film film) {
    return getFilmById(film.getId())
        .map(f -> {
          films.replace(film.getId(), film);
          return film;
        });
  }

  @Override
  public Optional<Film> getFilmById(final Long id) {
    return Optional.ofNullable(films.get(id));
  }

  @Override
  public Stream<Film> getFilms() {
    return films.values().stream();
  }
}
