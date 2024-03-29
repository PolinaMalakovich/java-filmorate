package ru.yandex.practicum.filmorate.controllers;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

@RestController
@RequestMapping("/genres")
@Validated
public class GenreController {
  private final GenreService genreService;

  @Autowired
  public GenreController(GenreService genreService) {
    this.genreService = genreService;
  }

  @GetMapping("/{id}")
  public Genre getGenre(@PathVariable final Long id) {
    return genreService.getGenreById(id);
  }

  @GetMapping
  public List<Genre> getGenres() {
    return genreService.getGenres().collect(Collectors.toList());
  }
}
