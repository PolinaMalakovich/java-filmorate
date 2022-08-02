package ru.yandex.practicum.filmorate.service;

import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

@Service
@Slf4j
public final class GenreService {
  private final GenreStorage genreStorage;

  @Autowired
  public GenreService(GenreStorage genreStorage) {
    this.genreStorage = genreStorage;
  }

  public Genre addGenre(final Genre newGenre) {
    final Genre genre = genreStorage.addGenre(newGenre).orElseThrow(IllegalArgumentException::new);
    log.info("New genre created successfully");

    return genre;
  }

  public Genre getGenreById(final Long id) {
    return genreStorage.getGenreById(id)
        .orElseThrow(() -> new EntityNotFoundException("Genre", id));
  }

  public Stream<Genre> getGenres() {
    return genreStorage.getGenres();
  }
}
