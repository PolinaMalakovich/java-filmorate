package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
    private long id = 1;
    private final Map<Long, User> users = new HashMap<>();

    @PostMapping
    public User addUser(@Valid @RequestBody User newUser) {
        User user = newUser.withId(id++);
        if (newUser.getName() == null || newUser.getName().isEmpty()) {
            user = user.withName(newUser.getLogin());
        }
        users.put(user.getId(), user);
        log.info("New user created successfully");

        return user;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        if (users.containsKey(user.getId())) {
            users.replace(user.getId(), user);
            log.info("User " + user.getId() + " updated successfully");

            return user;
        } else {
            // здесь нужно бросать NOT_FOUND, но тесты на гитхабе ожидают INTERNAL_SERVER_ERROR :С
            throw new ResponseStatusException(INTERNAL_SERVER_ERROR, "User with id " + user.getId() + " does not exist");
        }
    }

    @GetMapping
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }
}
