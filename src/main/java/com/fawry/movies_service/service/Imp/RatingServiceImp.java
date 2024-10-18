package com.fawry.movies_service.service.Imp;

import com.fawry.movies_service.entities.Movie;
import com.fawry.movies_service.entities.Rating;
import com.fawry.movies_service.entities.User;
import com.fawry.movies_service.exc.GenericErrorResponse;
import com.fawry.movies_service.repo.MovieRepository;
import com.fawry.movies_service.repo.RatingRepository;
import com.fawry.movies_service.repo.UserRepository;
import com.fawry.movies_service.service.RatingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class RatingServiceImp implements RatingService {

    @Autowired
    private RatingRepository ratingRepository;

    @Autowired
    private MovieRepository movieRepository;

    @Autowired
    private UserRepository userRepository;


    @Override
    public void addRating(String imbdId, Double ratingValue, Long userId) {
        Movie movie = movieRepository.findByImdbID(imbdId)
                .orElseThrow(() -> new GenericErrorResponse("Movie not found", HttpStatus.NOT_FOUND));
        Optional<User> user = userRepository.findById(userId);
        userId = user.get().getId();

        Optional<Rating> existingRating = ratingRepository.findByMovieIdAndUserId(movie.getId(), userId);
        if (existingRating.isPresent()) {
            Rating rating = existingRating.get();
            rating.setRating(ratingValue);
            ratingRepository.save(rating);
            updateAppRating(movie.getId());
            return;
        }
        Rating rating = new Rating();
        rating.setMovie(movie);
        rating.setRating(ratingValue);

        rating.setUser(user.orElse(null));

        ratingRepository.save(rating);

        updateAppRating(movie.getId());
    }

    @Override
    public void deleteRating(Long movieId) {
        Optional<Movie> movie = movieRepository.findById(movieId);

        if (movie.isPresent()) {
            ratingRepository.deleteByMovieId(movie.get().getId());
            updateAppRating(movieId);
        }
    }



    private void updateAppRating(Long movieId) {
        List<Rating> ratings = ratingRepository.findByMovieId(movieId);
        double averageRating = ratings.stream()
                .mapToDouble(Rating::getRating)
                .average()
                .orElse(0.0);

        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new GenericErrorResponse("Movie not found", HttpStatus.NOT_FOUND));
        movie.setAppRating(averageRating);
        movieRepository.save(movie);
    }
}
