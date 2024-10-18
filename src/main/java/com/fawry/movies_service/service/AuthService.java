package com.fawry.movies_service.service;

import com.fawry.movies_service.dto.LoginResponseDto;
import com.fawry.movies_service.entities.User;
import com.fawry.movies_service.dto.LoginRequestDto;
import com.fawry.movies_service.dto.RegisterRequestDto;

public interface AuthService {

    LoginResponseDto login(LoginRequestDto request);

    User register(RegisterRequestDto request);
}
