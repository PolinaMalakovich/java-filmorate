package ru.yandex.practicum.filmorate.controllers;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

@RestController
@RequestMapping("/mpa")
@Validated
public class MpaController {
  private final MpaService mpaService;

  @Autowired
  public MpaController(MpaService mpaService) {
    this.mpaService = mpaService;
  }

  @GetMapping("/{id}")
  public Mpa getMpa(@PathVariable final Long id) {
    return mpaService.getMpaById(id);
  }

  @GetMapping
  public List<Mpa> getMpas() {
    return mpaService.getMpas().collect(Collectors.toList());
  }
}
