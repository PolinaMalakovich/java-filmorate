package ru.yandex.practicum.filmorate.model;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.LocalDate;
import java.time.Month;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.yandex.practicum.filmorate.testutils.TestUtils.violationsToMap;

class UserTest {
    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    public static void createValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    public static void close() {
        validatorFactory.close();
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = { "" })
    public void emailCannotBeNullOrEmpty(String email) {
        User user = new User(
                0L,
                email,
                "login",
                "name",
                LocalDate.of(2021, Month.JUNE, 26),
                new HashSet<>());

        Map<String, Set<String>> expected = Map.ofEntries(
                Map.entry("email", Set.of("Email cannot be null or empty"))
        );

        assertEquals(expected, violationsToMap(validator.validate(user)));
    }

    @ParameterizedTest
    @ValueSource(strings = { " ", "\t", "\n", "\r", "@email.com", "email@.com" })
    public void emailValidation(String email) {
        User user = new User(
                0L,
                email,
                "login",
                "name",
                LocalDate.of(2021, Month.JUNE, 26),
                new HashSet<>());

        Map<String, Set<String>> expected = Map.ofEntries(
                Map.entry("email", Set.of("Email should be valid"))
        );

        assertEquals(expected, violationsToMap(validator.validate(user)));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = { "", " ", "\t", "\n", "\r" })
    public void loginValidation(String login) {
        User user = new User(
                0L,
                "example@gmail.com",
                login,
                "name",
                LocalDate.of(2021, Month.JUNE, 26),
                new HashSet<>());

        Map<String, Set<String>> expected = Map.ofEntries(
                Map.entry("login", Set.of("Login cannot be blank"))
        );

        assertEquals(expected, violationsToMap(validator.validate(user)));
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = { "", " ", "\t", "\n", "\r" })
    public void nameValidation(String name) {
        User user = new User(
                0L,
                "example@gmail.com",
                "login",
                name,
                LocalDate.of(2021, Month.JUNE, 26),
                new HashSet<>());

        assertEquals(user.getLogin(), user.getName());
    }

    @Test
    public void birthdayCannotBeInTheFuture() {
        User user = new User(
                0L,
                "example@gmail.com",
                "login",
                "name",
                LocalDate.of(2023, Month.JUNE, 26),
                new HashSet<>());

        Map<String, Set<String>> expected = Map.ofEntries(
                Map.entry("birthday", Set.of("Birthday should be in the past"))
        );

        assertEquals(expected, violationsToMap(validator.validate(user)));
    }
}