package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class Genre {
  Long id;

  @NotBlank
  String name;
}
