package ru.yandex.practicum.filmorate.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;

public class FilmDurationValidator implements ConstraintValidator<FilmDuration, Duration> {
    @Override
    public void initialize(FilmDuration constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(Duration duration, ConstraintValidatorContext constraintValidatorContext) {
        return !duration.isNegative();
    }
}
