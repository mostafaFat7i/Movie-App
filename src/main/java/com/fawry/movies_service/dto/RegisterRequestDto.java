package com.fawry.movies_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RegisterRequestDto {
    private String username;
    private String password;
    private String email;
}
