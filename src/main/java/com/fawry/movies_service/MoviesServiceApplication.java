package com.fawry.movies_service;

import com.fawry.movies_service.entities.Role;
import com.fawry.movies_service.entities.User;
import com.fawry.movies_service.repo.RoleRepository;
import com.fawry.movies_service.repo.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@SpringBootApplication
@EnableJpaAuditing
public class MoviesServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoviesServiceApplication.class, args);
    }
    @Bean
    CommandLineRunner initRolesAndAdminUser(RoleRepository roleRepo,
                                            UserRepository userRepo,
                                            PasswordEncoder passwordEncoder) {
        return args -> {

            if (!roleRepo.findByName("ADMIN").isPresent()) {
                roleRepo.save(new Role(null, "ADMIN"));
            }

            if (!roleRepo.findByName("USER").isPresent()) {
                roleRepo.save(new Role(null, "USER"));
            }

            if (!userRepo.existsByEmail("admin")) {
                Role adminRole = roleRepo.findByName("ADMIN")
                        .orElseThrow(() -> new RuntimeException("Admin role not found"));

                User adminUser = User.builder()
                        .email("admin")
                        .password(passwordEncoder.encode("admin5544"))
                        .name("Admin")
                        .roles(Set.of(adminRole))
                        .build();

                userRepo.save(adminUser);
                System.out.println("Admin user created with email: admin and password: admin5544");
            } else {
                System.out.println("Admin user already exists. Skipping creation.");
            }
        };
    }

}
