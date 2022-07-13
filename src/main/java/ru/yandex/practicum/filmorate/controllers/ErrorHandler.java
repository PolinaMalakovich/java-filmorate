package ru.yandex.practicum.filmorate.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;

import javax.validation.ValidationException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestControllerAdvice
public final class ErrorHandler {
    @ExceptionHandler
    public ResponseEntity<String> handleValidationException(final ValidationException e) {
        return new ResponseEntity<>(e.getMessage(), BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleEntityNotFoundException(final EntityNotFoundException e) {
        return new ResponseEntity<>(e.getMessage(), NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<String> handleThrowable(final Throwable e) {
        return new ResponseEntity<>(e.getMessage(), INTERNAL_SERVER_ERROR);
    }
}
