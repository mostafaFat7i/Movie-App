package com.fawry.movies_service.dto;

import com.fawry.movies_service.entities.Movie;
import lombok.*;

import java.util.List;


@Builder
public record PaginatedMoviesResponse(List<Movie> movies, int totalPages, long totalItems) {
}
