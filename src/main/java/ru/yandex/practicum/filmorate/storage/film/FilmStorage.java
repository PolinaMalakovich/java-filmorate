package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.stream.Stream;

public interface FilmStorage {
    Film addFilm(Film newFilm);

    Film updateFilm(Film film);

    Film getFilmById(Long id);

    Stream<Film> getFilms();
}
