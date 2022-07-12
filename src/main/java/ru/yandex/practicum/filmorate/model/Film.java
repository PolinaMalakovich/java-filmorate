package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Value;
import lombok.With;
import ru.yandex.practicum.filmorate.validation.FilmDuration;
import ru.yandex.practicum.filmorate.validation.FilmReleaseDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Stream;

@With
@Value
public class Film {
    Long id;
    Set<Long> likes;

    @NotBlank(message = "Name cannot be blank")
    String name;

    @Size(max = 200, message = "Description cannot be longer than 200 characters")
    String description;

    @FilmReleaseDate
    LocalDate releaseDate;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    @FilmDuration
    Duration duration;

    public Stream<Long> getLikes() {
        return likes != null ? likes.stream() : Stream.empty();
    }
}
