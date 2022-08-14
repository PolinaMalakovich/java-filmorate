package ru.yandex.practicum.filmorate.service;

import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

@Service
@Slf4j
public class MpaService {
  private final MpaStorage mpaStorage;

  @Autowired
  public MpaService(MpaStorage mpaStorage) {
    this.mpaStorage = mpaStorage;
  }

  public Mpa addMpa(final Mpa newMpa) {
    final Mpa mpa =
        mpaStorage.addMpa(newMpa).orElseThrow(IllegalArgumentException::new);
    log.info("New rating created successfully");

    return mpa;
  }

  public Mpa getMpaById(final Long id) {
    return mpaStorage.getMpaById(id)
        .orElseThrow(() -> new EntityNotFoundException("Rating", id));
  }

  public Stream<Mpa> getMpas() {
    return mpaStorage.getMpas();
  }
}
