package com.fawry.movies_service.service;

import com.fawry.movies_service.dto.MoviesDto;
import com.fawry.movies_service.dto.PaginatedMoviesResponse;
import com.fawry.movies_service.entities.Movie;

import java.util.List;

public interface MovieService {


    void removeMovie(String imdbID);


    Movie addMovieToDB(String imdbID);


    List<MoviesDto> searchMovies(String title);
    PaginatedMoviesResponse searchMoviesInDB(String title, Integer page, Integer size);


    void removeBatchOfMoviesFromDB(List<String> moviesImdbIDs);
    List<Movie> addBatchOfMoviesFromDB(List<String> moviesImdbIDs);

    PaginatedMoviesResponse getAllMoviesFromDB(int page, int size);

    Movie getMovieByImdbID(String imdbID);


}
