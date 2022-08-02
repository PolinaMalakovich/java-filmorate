package ru.yandex.practicum.filmorate.storage.like;

import java.util.stream.Stream;
import ru.yandex.practicum.filmorate.model.Film;

public interface LikeStorage {
  Stream<Long> addLikes(Film film);

  Stream<Long> getLikes(Long filmId);

  Stream<Long> updateLikes(Film film);
}
