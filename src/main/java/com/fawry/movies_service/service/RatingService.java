package com.fawry.movies_service.service;

import java.util.List;
import java.util.Map;

public interface RatingService {
    void addRating(String imbdId, Double ratingValue,Long movieId);
    void deleteRating(Long movieId);
}
