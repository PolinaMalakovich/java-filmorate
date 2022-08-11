package ru.yandex.practicum.filmorate.service;

import static java.util.stream.Collectors.toSet;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.SaveException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.follow.FollowStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

@Service
@Slf4j
public final class UserService {
  private final UserStorage userStorage;
  private final FollowStorage followStorage;

  @Autowired
  public UserService(@Qualifier("dbUserStorage") final UserStorage userStorage,
                     FollowStorage followStorage) {
    this.userStorage = userStorage;
    this.followStorage = followStorage;
  }

  public User addUser(final User newUser) {
    Long id = userStorage.addUser(newUser)
        .map(user -> {
          followStorage.addFollowers(user.getId(), newUser.getFriends());
          return user.getId();
        })
        .orElseThrow(() -> new SaveException("User"));
    log.info("New user created successfully");

    return getUserById(id);
  }

  public User updateUser(final User user) {
    Long id = userStorage.updateUser(user)
        .map(updated -> {
          followStorage.updateFollowers(updated.getId(), user.getFriends());
          return updated.getId();
        })
        .orElseThrow(() -> new SaveException("User"));
    log.info("User " + user.getId() + " updated successfully");

    return getUserById(id);
  }

  public User getUserById(final Long id) {
    return userStorage.getUserById(id)
        .map(user -> user.withFriends(followStorage.getFollowers(id).collect(toSet())))
        .orElseThrow(() -> new EntityNotFoundException("User", id));
  }

  public Stream<User> getUsers() {
    return userStorage.getUsers()
        .map(user -> user.withFriends(followStorage.getFollowers(user.getId()).collect(toSet())));
  }

  public User addFriend(final Long id, final Long friendId) {
    return updateUser(addOrDeleteFriendHelper(id, friendId, true));
  }

  public User deleteFriend(final Long id, final Long friendId) {
    return updateUser(addOrDeleteFriendHelper(id, friendId, false));
  }

  private User addOrDeleteFriendHelper(final Long id,
                                       final Long friendId,
                                       final boolean shouldAdd) {
    final User user = getUserById(id);
    final User friend = getUserById(friendId);
    Set<Long> friends = user.getFriends().collect(toSet());

    if (shouldAdd) {
      friends.add(friend.getId());
    } else {
      friends.remove(friend.getId());
    }

    return user.withFriends(friends);
  }

  public Stream<User> getMutualFriends(final Long id, final Long otherId) {
    final User user = getUserById(id);
    final User other = getUserById(otherId);
    Set<Long> mutualFriends = user.getFriends().collect(Collectors.toSet());
    mutualFriends.retainAll(other.getFriends().collect(toSet()));

    return mutualFriends.stream().map(this::getUserById);
  }

  public Stream<User> getFriends(final Long id) {
    return getUserById(id)
        .getFriends()
        .map(this::getUserById);
  }
}
