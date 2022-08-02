package ru.yandex.practicum.filmorate.storage.mpa;

import java.util.Optional;
import java.util.stream.Stream;
import ru.yandex.practicum.filmorate.model.Mpa;

public interface MpaStorage {
  Optional<Mpa> addMpa(Mpa mpa);

  Optional<Mpa> getMpaById(Long id);

  Stream<Mpa> getMpas();
}
