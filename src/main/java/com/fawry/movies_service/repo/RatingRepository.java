package com.fawry.movies_service.repo;

import com.fawry.movies_service.entities.Rating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByMovieId(Long movieId);
    Optional<Rating> findByMovieIdAndUserId(Long movieId, Long userId);
    List<Rating> findByUserId(Long userId);
    @Transactional
    void deleteByMovieId(Long movieId);
}
