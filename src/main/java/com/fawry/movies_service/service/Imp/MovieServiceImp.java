package com.fawry.movies_service.service.Imp;

import com.fasterxml.jackson.databind.JsonNode;

import com.fawry.movies_service.dto.MoviesDto;
import com.fawry.movies_service.dto.PaginatedMoviesResponse;
import com.fawry.movies_service.entities.Movie;
import com.fawry.movies_service.exc.GenericErrorResponse;
import com.fawry.movies_service.exc.MovieNotFoundException;
import com.fawry.movies_service.repo.MovieRepository;
import com.fawry.movies_service.repo.RatingRepository;
import com.fawry.movies_service.service.MovieService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MovieServiceImp implements MovieService {

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private RatingRepository ratingRepository;

    @Value("${omdb.api.key}")
    private String omdbApiKey;

    @Value("${omdb.api.url}")
    private String omdbApiUrl;

    private final Logger logger = LoggerFactory.getLogger(MovieServiceImp.class);


    @Override
    public List<MoviesDto> searchMovies(String title) {
        String url = String.format("%s/?apikey=%s&s=%s&type=movie", omdbApiUrl, omdbApiKey, title);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<JsonNode> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, JsonNode.class);

        JsonNode jsonResponse = responseEntity.getBody();
        List<MoviesDto> movies = new ArrayList<>();

        // Assuming the JSON structure has a "Search" field which is an array of movie objects
        if (jsonResponse != null && jsonResponse.has("Search")) {
            for (JsonNode movieNode : jsonResponse.get("Search")) {
                movies.add(MoviesDto.builder().imdbID(
                                movieNode.get("imdbID").asText())
                        .poster(movieNode.get("Poster").asText())
                        .title(movieNode.get("Title").asText())
                        .year(movieNode.get("Year").asText()).build());
            }
        }

        return movies;
    }

    @Override
    public PaginatedMoviesResponse searchMoviesInDB(String title,Integer pageNum, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNum, pageSize);
        Page<Movie> moviePage = movieRepository.findByTitleContainingIgnoreCase(title, pageable);
        List<Movie> movies = moviePage.getContent();

        return PaginatedMoviesResponse.builder()
                .movies(movies)
                .totalPages(moviePage.getTotalPages())
                .totalItems(moviePage.getTotalElements())
                .build();
    }

    @Override
    @Transactional
    public void removeBatchOfMoviesFromDB(List<String> moviesImdbIDs) {
        List<Movie> moviesToRemove = movieRepository.findAllByImdbIDIn(moviesImdbIDs);
        if (moviesToRemove.isEmpty()) {
            throw new GenericErrorResponse("No movies found for the given IMDb IDs", HttpStatus.NOT_FOUND);
        }
        movieRepository.deleteAll(moviesToRemove);
    }

    @Override
    @Transactional
    public List<Movie> addBatchOfMoviesFromDB(List<String> moviesImdbIDs) {
        List<String> existingMovies = movieRepository.findAllByImdbIDIn(moviesImdbIDs).stream()
                .map(Movie::getImdbID)
                .toList();

        return moviesImdbIDs.stream()
                .filter(id -> !existingMovies.contains(id))
                .map(this::addMovieToDB)
                .collect(Collectors.toList());
    }



    @Override
    public PaginatedMoviesResponse getAllMoviesFromDB(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Movie> moviePage = movieRepository.findAll(pageable);

        List<Movie> movies = moviePage.getContent();

        return PaginatedMoviesResponse.builder()
                .movies(movies)
                .totalPages(moviePage.getTotalPages())
                .totalItems(moviePage.getTotalElements())
                .build();
    }

    @Override
    public Movie getMovieByImdbID(String imdbID) {

        return movieRepository.findByImdbID(imdbID)
                .orElseThrow(() -> {
                    logger.error("Movie with IMDb ID: {} not found", imdbID);
                    return new MovieNotFoundException("Movie with IMDb ID: " + imdbID + " not found.");
                });
    }


    @Override
    public Movie addMovieToDB(String imdbID) {
        String url = String.format("%s/?&i=%s&apikey=%s", omdbApiUrl, imdbID, omdbApiKey);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<JsonNode> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, JsonNode.class);
            JsonNode jsonResponse = Optional.ofNullable(responseEntity.getBody())
                    .orElseThrow(() -> new GenericErrorResponse("Empty response from OMDB API", HttpStatus.BAD_GATEWAY));

            if (movieRepository.findByImdbID(jsonResponse.get("imdbID").asText()).isPresent()) {
                logger.error("Movie with IMDb ID: {} already exists", jsonResponse.get("imdbID").asText());
                throw new GenericErrorResponse("Movie with IMDb ID: " + jsonResponse.get("imdbID").asText() + " already exists", HttpStatus.CONFLICT);
            }

            Movie movie = mapJsonToMovie(jsonResponse);
            return movieRepository.save(movie);

        } catch (GenericErrorResponse e) {
            logger.error("Failed to add movie to DB for IMDb ID {}: {}", imdbID, e.getMessage(), e);
            throw e;
        } catch (Exception e) {
            logger.error("Failed to add movie to DB for IMDb ID {}: {}", imdbID, e.getMessage(), e);
            throw new GenericErrorResponse("Failed to add movie to DB", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Movie mapJsonToMovie(JsonNode jsonResponse) {
        Movie movie = new Movie();

        movie.setImdbID(jsonResponse.get("imdbID").asText());
        movie.setTitle(jsonResponse.get("Title").asText());
        movie.setYear(jsonResponse.get("Year").asText());
        movie.setReleased(jsonResponse.get("Released").asText());
        movie.setRuntime(jsonResponse.get("Runtime").asText());
        movie.setGenre(jsonResponse.get("Genre").asText());
        movie.setDirector(jsonResponse.get("Director").asText());
        movie.setWriter(jsonResponse.get("Writer").asText());
        movie.setActors(jsonResponse.get("Actors").asText());
        movie.setPlot(jsonResponse.get("Plot").asText());
        movie.setLanguage(jsonResponse.get("Language").asText());
        movie.setCountry(jsonResponse.get("Country").asText());
        movie.setAwards(jsonResponse.get("Awards").asText());
        movie.setRated(jsonResponse.get("Rated").asText());
        movie.setImdbRating(jsonResponse.get("imdbRating").asText());
        movie.setImdbVotes(jsonResponse.get("imdbVotes").asText());
        movie.setPoster(jsonResponse.get("Poster").asText());

        return movie;
    }

    @Override
    public void removeMovie(String imdbID) {
        Optional<Movie> movie = movieRepository.findByImdbID(imdbID);
        if (movie.isPresent()) {
            // Delete ratings associated with the movie first
            ratingRepository.deleteByMovieId(movie.get().getId());

            // Now delete the movie
            movieRepository.delete(movie.get());
            logger.info("Movie deleted successfully with id: {}", imdbID);
        } else {
            throw new GenericErrorResponse("Movie not found", HttpStatus.NOT_FOUND);
        }
    }

}
