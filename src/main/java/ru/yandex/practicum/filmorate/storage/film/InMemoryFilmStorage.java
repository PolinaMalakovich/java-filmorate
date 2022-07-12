package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

@Component
public final class InMemoryFilmStorage implements FilmStorage {
    private long id = 1;
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Film addFilm(final Film newFilm) {
        final Film film = newFilm.withId(id++);
        films.put(film.getId(), film);

        return film;
    }

    @Override
    public Film updateFilm(final Film film) {
        checkIfIdExists(film.getId());
        films.replace(film.getId(), film);
        return film;
    }

    @Override
    public Film getFilmById(final Long id) {
        checkIfIdExists(id);
        return films.get(id);
    }

    private void checkIfIdExists(final Long id) {
        if (!films.containsKey(id)) throw new EntityNotFoundException("Film", id);
    }

    @Override
    public Stream<Film> getFilms() {
        return films.values().stream();
    }
}
