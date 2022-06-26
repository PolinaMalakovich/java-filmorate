package ru.yandex.practicum.filmorate.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private static int id = 1;
    private final Map<Integer, Film> films = new HashMap<>();

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film newFilm) {
        final Film film = newFilm.withId(id++);
        films.put(film.getId(), film);
        log.info("New film created successfully");

        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        if (films.containsKey(film.getId())) {
            films.replace(film.getId(), film);
            log.info("Film " + film.getId() + " updated successfully");

            return film;
        } else {
            throw new ResponseStatusException(NOT_FOUND, "Film with id " + film.getId() + " does not exist");
        }
    }

    @GetMapping
    public List<Film> getFilms() {
        return new ArrayList<>(films.values());
    }
}
