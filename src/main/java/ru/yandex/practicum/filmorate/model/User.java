package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.With;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Past;
import java.time.LocalDate;

@With
@Data
public class User {
    private final Long id;

    @NotEmpty(message = "Email cannot be null or empty")
    @Email(message = "Email should be valid")
    private final String email;

    @NotBlank(message = "Login cannot be blank")
    private final String login;

    private final String name;

    @Past(message = "Birthday should be in the past")
    private final LocalDate birthday;

    public User(Long id, String email, String login, String name, LocalDate birthday) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name == null || name.isEmpty() || name.isBlank() ? login : name;
        this.birthday = birthday;
    }
}
