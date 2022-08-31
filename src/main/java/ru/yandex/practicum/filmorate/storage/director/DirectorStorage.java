package ru.yandex.practicum.filmorate.storage.director;

import java.util.Optional;
import java.util.stream.Stream;
import ru.yandex.practicum.filmorate.model.Director;

public interface DirectorStorage {
  Optional<Director> addDirector(Director director);

  Optional<Director> getDirector(Long id);

  Stream<Director> getDirectors();

  Optional<Director> updateDirector(Director director);

  void deleteDirector(Long id);
}
