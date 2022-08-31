package ru.yandex.practicum.filmorate.controllers;

import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

@RestController
@RequestMapping("/directors")
@Validated
public class DirectorController {
  private final DirectorService directorService;

  @Autowired
  public DirectorController(final DirectorService directorService) {
    this.directorService = directorService;
  }

  @PostMapping
  public Director addDirector(@Valid @RequestBody Director director) {
    return directorService.addDirector(director);
  }

  @GetMapping("/{id}")
  public Director getDirector(@PathVariable final Long id) {
    return directorService.getDirector(id);
  }

  @GetMapping
  public List<Director> getDirectors() {
    return directorService.getDirectors().collect(Collectors.toList());
  }

  @PutMapping
  public Director updateDirector(@Valid @RequestBody Director director) {
    return directorService.updateDirector(director);
  }

  @DeleteMapping("/{id}")
  public void deleteDirector(@PathVariable final Long id) {
    directorService.deleteDirector(id);
  }
}
