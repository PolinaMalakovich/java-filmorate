package ru.yandex.practicum.filmorate.exception;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = false)
public class SaveException extends RuntimeException {
  String entity;

  public SaveException(String entity) {
    super(entity + " could not be saved");
    this.entity = entity;
  }
}
