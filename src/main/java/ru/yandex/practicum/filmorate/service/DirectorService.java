package ru.yandex.practicum.filmorate.service;

import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.SaveException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

@Service
@Slf4j
public class DirectorService {
  private final DirectorStorage directorStorage;

  @Autowired
  public DirectorService(DirectorStorage directorStorage) {
    this.directorStorage = directorStorage;
  }

  public Director addDirector(final Director director) {
    return directorStorage.addDirector(director).orElseThrow(() -> new SaveException("Director"));
  }

  public Director getDirector(final Long id) {
    return directorStorage.getDirector(id).orElseThrow(() -> new EntityNotFoundException("Director", id));
  }

  public Stream<Director> getDirectors() {
    return directorStorage.getDirectors();
  }

  public Director updateDirector(final Director director) {
    return directorStorage.updateDirector(director).orElseThrow(() -> new SaveException("Director"));
  }

  public void deleteDirector(final Long id) {
    directorStorage.deleteDirector(id);
  }
}
