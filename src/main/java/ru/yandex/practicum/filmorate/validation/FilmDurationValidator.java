package ru.yandex.practicum.filmorate.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.Duration;

public final class FilmDurationValidator implements ConstraintValidator<FilmDuration, Duration> {
    @Override
    public void initialize(final FilmDuration constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(final Duration duration, final ConstraintValidatorContext constraintValidatorContext) {
        return !duration.isNegative();
    }
}
