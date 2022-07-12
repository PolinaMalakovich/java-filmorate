package ru.yandex.practicum.filmorate.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.Month;

public final class FilmReleaseDateValidator implements ConstraintValidator<FilmReleaseDate, LocalDate> {
    public static final LocalDate FIRST_FILM = LocalDate.of(1895, Month.DECEMBER, 28);

    @Override
    public void initialize(final FilmReleaseDate constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(final LocalDate releaseDate, final ConstraintValidatorContext constraintValidatorContext) {
        return releaseDate.isAfter(FIRST_FILM) || releaseDate.equals(FIRST_FILM);
    }
}
