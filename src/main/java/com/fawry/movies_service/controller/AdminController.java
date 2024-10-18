package com.fawry.movies_service.controller;

import com.fawry.movies_service.dto.MoviesDto;
import com.fawry.movies_service.entities.Movie;
import com.fawry.movies_service.service.MovieService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private MovieService movieService;

    @GetMapping("/dashboard")
    public ResponseEntity<List<MoviesDto>> adminDashboard(@RequestParam String title) {
        return new ResponseEntity<>(movieService.searchMovies(title), HttpStatus.OK);
    }

    @PostMapping("/dashboard/add")
    public ResponseEntity<Movie> addMovieToDB(@RequestParam("imdbID") String imdbID) {
        return new ResponseEntity<>(movieService.addMovieToDB(imdbID), HttpStatus.OK);
    }

    @DeleteMapping("/dashboard/batch-remove")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> batchRemove(@RequestBody List<String> moviesImdbIDs) {
        if (moviesImdbIDs == null || moviesImdbIDs.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        movieService.removeBatchOfMoviesFromDB(moviesImdbIDs);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/dashboard/batch-add")
    public ResponseEntity<List<Movie>> addMovies(@RequestBody List<String> moviesImdbIDs) {
        if (moviesImdbIDs == null || moviesImdbIDs.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        List<Movie> addedMovies = movieService.addBatchOfMoviesFromDB(moviesImdbIDs);
        return new ResponseEntity<>(addedMovies, HttpStatus.CREATED);
    }



    @DeleteMapping("/dashboard/remove")
    public void removeMovie(@RequestParam("imdbID") String imdbID) {
        movieService.removeMovie(imdbID);
    }


}
