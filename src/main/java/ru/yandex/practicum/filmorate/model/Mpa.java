package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.NotBlank;
import lombok.Value;

@Value
public class Mpa {
  Long id;

  @NotBlank
  String name;
}
