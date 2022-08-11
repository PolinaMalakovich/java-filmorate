package ru.yandex.practicum.filmorate.storage.genre;

import java.util.Optional;
import java.util.stream.Stream;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

public interface GenreStorage {
  Optional<Genre> addGenre(Genre genre);

  Stream<Genre> addGenres(Long id, Stream<Genre> genres);

  Optional<Genre> getGenreById(Long genreId);

  Stream<Genre> getGenres();

  Stream<Genre> getGenresByFilmId(Long filmId);

  Stream<Genre> updateGenres(Long id, Stream<Genre> genres);
}
