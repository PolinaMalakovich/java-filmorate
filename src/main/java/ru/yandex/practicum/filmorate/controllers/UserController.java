package ru.yandex.practicum.filmorate.controllers;

import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {
  private final UserService userService;

  @Autowired
  public UserController(final UserService userService) {
    this.userService = userService;
  }

  @PostMapping
  public User addUser(@Valid @RequestBody final User newUser) {
    return userService.addUser(newUser);
  }

  @PutMapping
  public User updateUser(@Valid @RequestBody final User user) {
    return userService.updateUser(user);
  }

  @GetMapping("/{id}")
  public User getUser(@PathVariable final Long id) {
    return userService.getUserById(id);
  }

  @GetMapping
  public List<User> getUsers() {
    return userService.getUsers().collect(Collectors.toList());
  }

  @PutMapping("/{id}/friends/{friendId}")
  public User addFriend(@PathVariable final Long id, @PathVariable final Long friendId) {
    return userService.addFriend(id, friendId);
  }

  @DeleteMapping("/{id}/friends/{friendId}")
  public User deleteFriend(@PathVariable final Long id, @PathVariable final Long friendId) {
    return userService.deleteFriend(id, friendId);
  }

  @GetMapping("/{id}/friends/common/{otherId}")
  public List<User> getMutualFriends(@PathVariable final Long id,
                                     @PathVariable final Long otherId) {
    return userService.getMutualFriends(id, otherId).collect(Collectors.toList());
  }

  @GetMapping("/{id}/friends")
  public List<User> getFriends(@PathVariable final Long id) {
    return userService.getFriends(id).collect(Collectors.toList());
  }
}
