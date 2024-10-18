package com.fawry.movies_service.dto;

import lombok.Builder;

@Builder
public record MoviesDto(String imdbID,String title,String year, String poster,double rate) {
}
