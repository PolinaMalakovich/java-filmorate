package ru.yandex.practicum.filmorate.storage.follow;

import java.util.stream.Stream;
import ru.yandex.practicum.filmorate.model.User;

public interface FollowStorage {
  Stream<Long> addFollowers(Long id, Stream<Long> followers);

  Stream<Long> getFollowers(Long userId);

  Stream<Long> updateFollowers(Long id, Stream<Long> followers);
}
