package ru.yandex.practicum.filmorate.storage.genre;

import java.util.Optional;
import java.util.stream.Stream;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

public interface GenreStorage {
  Optional<Genre> addGenre(Genre genre);

  Stream<Genre> addGenres(Film film);

  Optional<Genre> getGenreById(Long genreId);

  Stream<Genre> getGenres();

  Stream<Genre> getGenresByFilmId(Long filmId);

  Stream<Genre> updateGenres(Film film);
}
