package ru.yandex.practicum.filmorate.controllers;

import static java.util.stream.Collectors.toMap;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static ru.yandex.practicum.filmorate.utils.StringUtils.suffix;

import java.util.Map;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.SaveException;

@RestControllerAdvice
public final class ErrorHandler {
  @ExceptionHandler
  public ResponseEntity<String> handleValidationException(final ValidationException e) {
    return new ResponseEntity<>(e.getMessage(), BAD_REQUEST);
  }

  @ExceptionHandler
  public ResponseEntity<Map<String, String>> handleConstraintViolationException(
      final ConstraintViolationException e
  ) {
    final Map<String, String> errorDetails = e.getConstraintViolations()
        .stream()
        .collect(toMap(
            violation -> suffix(violation.getPropertyPath().toString(), '.'),
            ConstraintViolation::getMessage
        ));

    return new ResponseEntity<>(errorDetails, BAD_REQUEST);
  }

  @ExceptionHandler
  public ResponseEntity<String> handleEntityNotFoundException(final EntityNotFoundException e) {
    return new ResponseEntity<>(e.getMessage(), NOT_FOUND);
  }

  @ExceptionHandler
  public ResponseEntity<String> handleThrowable(final Throwable e) {
    return new ResponseEntity<>(e.getMessage(), INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler
  public ResponseEntity<String> handleSaveException(final SaveException e) {
    return new ResponseEntity<>(e.getMessage(), NOT_FOUND);
  }
}
