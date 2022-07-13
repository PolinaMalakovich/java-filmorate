package ru.yandex.practicum.filmorate.model;

import lombok.Value;
import lombok.With;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Stream;

@With
@Value
public class User {
    Long id;

    @NotEmpty(message = "Email cannot be null or empty")
    @Email(message = "Email should be valid")
    String email;

    @NotBlank(message = "Login cannot be blank")
    String login;

    String name;

    @PastOrPresent(message = "Birthday should be in the past")
    LocalDate birthday;

    Set<Long> friends;

    public User(Long id, String email, String login, String name, LocalDate birthday, Set<Long> friends) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name == null || name.isBlank() ? login : name;
        this.birthday = birthday;
        this.friends = friends;
    }

    public Stream<Long> getFriends() {
        return friends != null ? friends.stream() : Stream.empty();
    }
}
