package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Comparator;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toSet;

@Service
@Slf4j
public final class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private static final Comparator<Film> MOST_LIKED_FILMS_FIRST =
            comparing((Film film) -> film.getLikes().count()).reversed();

    @Autowired
    public FilmService(final FilmStorage filmStorage, final UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public Film addFilm(final Film newFilm) {
        final Film film = filmStorage.addFilm(newFilm);
        log.info("New film created successfully");

        return film;
    }

    public Film updateFilm(final Film film) {
        final Film updated = filmStorage.updateFilm(film);
        log.info("Film " + film.getId() + " updated successfully");

        return updated;
    }

    public Film getFilmById(final Long id) { return filmStorage.getFilmById(id); }

    public Stream<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film addLike(final Long id, final Long userId) {
        final Film film = addOrDeleteLikeHelper(id, userId, true);
        return updateFilm(film);
    }

    public Film deleteLike(final Long id, final Long userId) {
        final Film film = addOrDeleteLikeHelper(id, userId, false);
        return updateFilm(film);
    }

    private Film addOrDeleteLikeHelper(final Long id, final Long userId, final boolean shouldAdd) {
        final User user = userStorage.getUserById(userId);
        final Film film = getFilmById(id);
        Set<Long> likes = film.getLikes().collect(toSet());

        if (shouldAdd) {
            likes.add(user.getId());
        } else {
            likes.remove(user.getId());
        }

        return film.withLikes(likes);
    }

    public Stream<Film> getMostPopularFilms(final Integer count) {
        return getFilms()
                .sorted(MOST_LIKED_FILMS_FIRST)
                .limit(count);
    }
}
