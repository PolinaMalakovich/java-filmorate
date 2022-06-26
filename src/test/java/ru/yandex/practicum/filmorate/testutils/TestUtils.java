package ru.yandex.practicum.filmorate.testutils;

import javax.validation.ConstraintViolation;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toSet;

public class TestUtils {
    public static <T> Map<String, Set<String>>  violationsToMap(final Set<ConstraintViolation<T>> violations) {
        return violations.stream()
                .collect(
                        groupingBy(x -> x.getPropertyPath().toString(),
                                mapping(ConstraintViolation::getMessage,
                                        toSet())
                        )
                );
    }
}
