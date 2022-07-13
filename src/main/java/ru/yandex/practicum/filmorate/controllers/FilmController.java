package ru.yandex.practicum.filmorate.controllers;

import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

@RestController
@RequestMapping("/films")
@Validated
public class FilmController {
  private final FilmService filmService;

  @Autowired
  public FilmController(final FilmService filmService) {
    this.filmService = filmService;
  }

  @PostMapping
  public Film addFilm(@Valid @RequestBody final Film newFilm) {
    return filmService.addFilm(newFilm);
  }

  @PutMapping
  public Film updateFilm(@Valid @RequestBody final Film film) {
    return filmService.updateFilm(film);
  }

  @GetMapping("/{id}")
  public Film getFilm(@PathVariable final Long id) {
    return filmService.getFilmById(id);
  }

  @GetMapping
  public List<Film> getFilms() {
    return filmService.getFilms().collect(Collectors.toList());
  }

  @PutMapping("/{id}/like/{userId}")
  public Film addLike(@PathVariable final Long id, @PathVariable final Long userId) {
    return filmService.addLike(id, userId);
  }

  @DeleteMapping("/{id}/like/{userId}")
  public Film deleteLike(@PathVariable final Long id, @PathVariable final Long userId) {
    return filmService.deleteLike(id, userId);
  }

  @GetMapping("/popular")
  public List<Film> getMostPopularFilms(
      @RequestParam(required = false, defaultValue = "10")
      @Min(1) final Integer count
  ) {
    return filmService.getMostPopularFilms(count).collect(Collectors.toList());
  }
}
