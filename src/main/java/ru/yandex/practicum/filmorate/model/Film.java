package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.With;
import ru.yandex.practicum.filmorate.validation.FilmDuration;
import ru.yandex.practicum.filmorate.validation.FilmReleaseDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.Duration;
import java.time.LocalDate;

@With
@Data
public class Film {
    private final Integer id;

    @NotBlank(message = "Name cannot be blank")
    private final String name;

    @Size(max = 200, message = "Description cannot be longer than 200 characters")
    private final String description;

    @FilmReleaseDate
    private final LocalDate releaseDate;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    @FilmDuration
    private final Duration duration;
}
