package ru.yandex.practicum.filmorate.storage.like;

import java.util.stream.Stream;
import ru.yandex.practicum.filmorate.model.Film;

public interface LikeStorage {
  void addLike(final Long id, final Long userId);

  void deleteLike(final Long id, final Long userId);

  Stream<Long> getLikes(Long filmId);
}
