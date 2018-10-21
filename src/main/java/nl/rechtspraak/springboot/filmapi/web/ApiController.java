package nl.rechtspraak.springboot.filmapi.web;

import nl.rechtspraak.springboot.filmapi.model.Film;
import nl.rechtspraak.springboot.filmapi.model.FilmlijstItem;
import nl.rechtspraak.springboot.filmapi.service.FilmService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Collection;
import java.util.Optional;

/**
 * Apicontroller voor de Film-api.
 */
@RestController
@RequestMapping("films")
public class ApiController {

    private FilmService filmService;

    /**
     * Constructor die films aanmaakt.
     */
    public ApiController(FilmService filmService) {
        this.filmService = filmService;
        maakFilms();
    }

    private void maakFilms() {
        final Film film1 = new Film();
        film1.setTitel("The Ususal Suspects");
        film1.setReleaseJaar(1995);
        film1.setDuur(Duration.ofMinutes(106));
        film1.setRegisseur("Bryan Singer");
        film1.addActeurs("Kevin Spacey", "Gabriel Byrne", "Chazz Palminteri");
        film1.setId("123");

        final Film film2 = new Film();
        film2.setTitel("Donnie Darko");
        film2.setReleaseJaar(2001);
        film2.setDuur(Duration.ofMinutes(113));
        film2.setRegisseur("Richard Kelly");
        film2.addActeurs("Jake Gyllenhaal", "Jena Malone", "Mary McDonnell");
        film2.setId("456");

        filmService.add(film1);
        filmService.add(film2);
    }

    @GetMapping
    public Collection<FilmlijstItem> getFilms() {
        return filmService.getFilms();
    }

    @GetMapping("{id}")
    public Film getFilm(@PathVariable("id") String id) {
        final Optional<Film> zoekResultaat = filmService.getFilm(id);
        if(zoekResultaat.isPresent()) {
            return zoekResultaat.get();
        } else {
            throw new FilmNietGevondenException(id);
        }
    }

    @PutMapping("{id}")
    public ResponseEntity<Film> setFilm(@PathVariable("id") String id, @RequestBody Film updateFilm) {
        final Optional<Film> optionalFilm = filmService.getFilm(id);
        if(optionalFilm.isPresent()) {
            Film currentFilm = optionalFilm.get();
            currentFilm.setDuur(updateFilm.getDuur());
            currentFilm.setRegisseur(updateFilm.getRegisseur());
            currentFilm.setReleaseJaar(updateFilm.getReleaseJaar());
            currentFilm.setTitel(updateFilm.getTitel());
            currentFilm.addActeurs(updateFilm.getActeurs().toArray(new String[0]));
            return new ResponseEntity<>(currentFilm, HttpStatus.ACCEPTED);
        } else {
            throw new FilmNietGevondenException(id);
        }
    }

    @DeleteMapping("{id}")
    public ResponseEntity<String> deleteFilm(@PathVariable("id") String id){
        final Optional<Film> zoekResultaat = filmService.getFilm(id);
        if(zoekResultaat.isPresent()) {
            filmService.removeFilm(id);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("DELETE Success!");
        } else {
            throw new FilmNietGevondenException(id);
        }
    }

    @PostMapping("{id}")
    public ResponseEntity<String> createFilm(@PathVariable("id") String id, @RequestBody Film newFilm){
        final Optional<Film> zoekResultaat = filmService.getFilm(id);
        if(zoekResultaat.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Resource already excists!");
        } else {
            newFilm.setId(id);
            filmService.createFilm(newFilm);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body("Film created!");
        }
    }

}
