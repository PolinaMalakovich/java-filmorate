package ru.yandex.practicum.filmorate.exception;

import lombok.EqualsAndHashCode;
import lombok.Value;

@Value
@EqualsAndHashCode(callSuper = false)
public class EntityNotFoundException extends RuntimeException {
    String entity;
    Long id;

    public EntityNotFoundException(final String entity, final Long id) {
        super(entity + " with id " + id + " does not exist");
        this.entity = entity;
        this.id = id;
    }
}
