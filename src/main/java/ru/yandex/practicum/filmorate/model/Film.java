package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Stream;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.With;
import ru.yandex.practicum.filmorate.validation.FilmDuration;
import ru.yandex.practicum.filmorate.validation.FilmReleaseDate;

@With
@Value
@EqualsAndHashCode(doNotUseGetters = true)
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

  Set<Genre> genres;

  Mpa mpa;

  public Stream<Long> getLikes() {
    return likes != null ? likes.stream() : Stream.empty();
  }
  public Stream<Genre> getGenres() {
    return genres != null ? genres.stream() : Stream.empty();
  }
}
