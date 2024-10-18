package com.fawry.movies_service.repo;

import com.fawry.movies_service.entities.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


public interface MovieRepository extends JpaRepository<Movie, Long> {
    Optional<Movie> findByImdbID(String imdbID);
    List<Movie> findAllByImdbIDIn(List<String> imdbIDs);

    Page<Movie> findByTitleContainingIgnoreCase(String title, Pageable pageable);

}
