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
import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.yandex.practicum.filmorate.testutils.TestUtils.violationsToMap;
import static ru.yandex.practicum.filmorate.validation.FilmReleaseDateValidator.FIRST_FILM;

class FilmTest {
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
    @ValueSource(strings = { "", " ", "\t", "\n", "\r" })
    public void nameValidation(String name) {
        Film film = new Film(
                0L,
                name,
                "description",
                LocalDate.of(2021, Month.JUNE, 26),
                Duration.of(2, ChronoUnit.HOURS));

        Map<String, Set<String>> expected = Map.ofEntries(
                Map.entry("name", Set.of("Name cannot be blank"))
        );

        assertEquals(expected, violationsToMap(validator.validate(film)));
    }

    @Test
    public void descriptionCannotBeLongerThan200Characters() {
        Film film = new Film(
                0L,
                "film",
                "The description of this film is way longer than it should be. " +
                        "It must not be longer than 200 characters, " +
                        "but in fact it exceeds the limit by 22 characters, " +
                        "which means that the length of this description is 222 characters.",
                LocalDate.of(2021, Month.JUNE, 26),
                Duration.of(2, ChronoUnit.HOURS));

        Map<String, Set<String>> expected = Map.ofEntries(
                Map.entry("description", Set.of("Description cannot be longer than 200 characters"))
        );

        assertEquals(expected, violationsToMap(validator.validate(film)));
    }

    @Test
    public void releaseDateCannotBeEarlierThan28December1895() {
        Film film = new Film(
                0L,
                "name",
                "description",
                FIRST_FILM.minusDays(1),
                Duration.of(2, ChronoUnit.HOURS));

        Map<String, Set<String>> expected = Map.ofEntries(
                Map.entry("releaseDate", Set.of("Release date cannot be earlier than December 28, 1895"))
        );

        assertEquals(expected, violationsToMap(validator.validate(film)));
    }
}