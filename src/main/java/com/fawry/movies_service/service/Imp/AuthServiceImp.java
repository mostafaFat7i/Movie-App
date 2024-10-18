package com.fawry.movies_service.service.Imp;


import com.fawry.movies_service.dto.LoginResponseDto;
import com.fawry.movies_service.entities.Role;
import com.fawry.movies_service.entities.User;
import com.fawry.movies_service.exc.EmailAlreadyExistsException;
import com.fawry.movies_service.exc.WrongCredentialsException;
import com.fawry.movies_service.repo.RoleRepository;
import com.fawry.movies_service.repo.UserRepository;
import com.fawry.movies_service.dto.LoginRequestDto;
import com.fawry.movies_service.dto.RegisterRequestDto;
import com.fawry.movies_service.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@Slf4j
public class AuthServiceImp implements AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository repo;

    @Autowired
    private JwtService jwtService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public LoginResponseDto login(LoginRequestDto request) {
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));


        log.info("Authentication successful for user: {}", request.getEmail());



        if (authenticate.isAuthenticated()) {
            Long userId = repo.findByEmail(request.getEmail()).get().getId();
            return LoginResponseDto
                    .builder()
                    .userId(userId)
                    .token(jwtService.generateToken(request.getEmail()))
                    .role(request.getEmail().equals("admin") ? "ADMIN" : "USER")
                    .build();
        }
        else throw new WrongCredentialsException("Wrong credentials");
    }


    @Override
    public User register(RegisterRequestDto request) {

        if (repo.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        // Fetch the 'USER' role from the database
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("USER role not found"));

        // Create and save the new user with the assigned role
        User user = User
                .builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getUsername())
                .roles(Collections.singleton(userRole)) // Assign the role
                .build();

        return repo.save(user);
    }

}
