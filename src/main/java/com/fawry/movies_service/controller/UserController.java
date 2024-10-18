package com.fawry.movies_service.controller;

import com.fawry.movies_service.dto.MoviesDto;
import com.fawry.movies_service.dto.PaginatedMoviesResponse;
import com.fawry.movies_service.entities.Movie;
import com.fawry.movies_service.service.MovieService;
import com.fawry.movies_service.service.RatingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private MovieService movieService;
    @Autowired
    private RatingService ratingService;

    @GetMapping("/dashboard")
    public ResponseEntity<PaginatedMoviesResponse> userDashboard(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        int pageNum = (page == null) ? 0 : page;
        int pageSize = (size == null) ? 5 : size;

        PaginatedMoviesResponse movies = movieService.getAllMoviesFromDB(pageNum, pageSize);
        return new ResponseEntity<>(movies, HttpStatus.OK);
    }

    @GetMapping("/dashboard/movie/{imdbID}")
    public ResponseEntity<Movie> getMovieDetails(@PathVariable String imdbID) {
        return new ResponseEntity<>(movieService.getMovieByImdbID(imdbID), HttpStatus.OK);
    }

    @PostMapping("/dashboard/rating/{imdbID}")
    public void addRating(@PathVariable String imdbID,
                          @RequestParam("rating") Double ratingValue,
                          @RequestParam("userId") Long userId) {
        ratingService.addRating(imdbID, ratingValue,userId);
    }

    @GetMapping("/dashboard/search")
    public ResponseEntity<PaginatedMoviesResponse> searchMovies(
            @RequestParam String title,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size
    ) {
        int pageNum = (page == null) ? 0 : page;
        int pageSize = (size == null) ? 5 : size;

        PaginatedMoviesResponse movies = movieService.searchMoviesInDB(title,pageNum, pageSize);
        return new ResponseEntity<>(movies, HttpStatus.OK);
    }
}
