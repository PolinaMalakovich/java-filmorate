package ru.yandex.practicum.filmorate.model;

import lombok.Value;

@Value
public class Follow {
  Long userId;
  Long friendId;
}
