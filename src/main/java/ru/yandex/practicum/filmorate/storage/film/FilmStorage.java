package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Optional;
import java.util.stream.Stream;

public interface FilmStorage {
    Film addFilm(Film newFilm);

    Optional<Film> updateFilm(Film film);

    Optional<Film> getFilmById(Long id);

    Stream<Film> getFilms();
}
