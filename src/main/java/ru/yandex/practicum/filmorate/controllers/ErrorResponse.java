package ru.yandex.practicum.filmorate.controllers;

import lombok.Value;

@Value
public class ErrorResponse {
    String error;
    String description;
}
