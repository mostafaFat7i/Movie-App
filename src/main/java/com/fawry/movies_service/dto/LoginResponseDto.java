package com.fawry.movies_service.dto;

import lombok.Builder;

@Builder
public record LoginResponseDto(Long userId, String token, String role) {}

