package ru.yandex.practicum.filmorate.service;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toSet;

import java.util.Comparator;
import java.util.Set;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.SaveException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.like.LikeStorage;

@Service
@Slf4j
public final class FilmService {
  private static final Comparator<Film> MOST_LIKED_FILMS_FIRST =
      comparing((Film film) -> film.getLikes().count()).reversed();
  private final FilmStorage filmStorage;
  private final LikeStorage likeStorage;
  private final GenreStorage genreStorage;
  private final UserService userService;

  @Autowired
  public FilmService(@Qualifier("DbFilmStorage") final FilmStorage filmStorage,
                     LikeStorage likeStorage, GenreStorage genreStorage,
                     final UserService userService) {
    this.filmStorage = filmStorage;
    this.likeStorage = likeStorage;
    this.genreStorage = genreStorage;
    this.userService = userService;
  }

  public Film addFilm(final Film newFilm) {
    Long id = filmStorage.addFilm(newFilm)
        .map(film -> {
          likeStorage.addLikes(film.getId(), newFilm.getLikes());
          genreStorage.addGenres(film.getId(), newFilm.getGenres());
          return film.getId();
        })
        .orElseThrow(() -> new SaveException("Film"));
    log.info("New film created successfully");

    return getFilmById(id);
  }

  public Film updateFilm(final Film film) {
    Long id = filmStorage.updateFilm(film)
        .map(updated -> {
          likeStorage.updateLikes(updated.getId(), film.getLikes());
          genreStorage.updateGenres(updated.getId(), film.getGenres());
          return updated.getId();
        })
        .orElseThrow(() -> new SaveException("Film"));
    log.info("Film " + film.getId() + " updated successfully");

    return getFilmById(id);
  }

  public Film getFilmById(final Long id) {
    return filmStorage.getFilmById(id)
        .map(film -> film
            .withLikes(likeStorage.getLikes(id).collect(toSet()))
            .withGenres(genreStorage.getGenresByFilmId(id).collect(toSet()))
        )
        .orElseThrow(() -> new EntityNotFoundException("Film", id));
  }

  public Stream<Film> getFilms() {
    return filmStorage.getFilms()
        .map(film -> film
            .withLikes(likeStorage.getLikes(film.getId()).collect(toSet()))
            .withGenres(genreStorage.getGenresByFilmId(film.getId()).collect(toSet()))
        );
  }

  public Film addLike(final Long id, final Long userId) {
    return addOrDeleteLikeHelper(id, userId, true);
  }

  public Film deleteLike(final Long id, final Long userId) {
    return addOrDeleteLikeHelper(id, userId, false);
  }

  private Film addOrDeleteLikeHelper(final Long id, final Long userId, final boolean shouldAdd) {
    final User user = userService.getUserById(userId);
    final Film film = getFilmById(id);
    Set<Long> likes = film.getLikes().collect(toSet());

    if (shouldAdd) {
      likes.add(user.getId());
    } else {
      likes.remove(user.getId());
    }
    likeStorage.updateLikes(film.getId(), likes.stream());

    return getFilmById(film.getId());
  }

  public Stream<Film> getMostPopularFilms(final Integer count) {
    return getFilms()
        .sorted(MOST_LIKED_FILMS_FIRST)
        .limit(count);
  }
}
