package com.fawry.movies_service.config;

import com.fawry.movies_service.entities.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;


//public class CustomUserDetails implements UserDetails {
//
//    private String email;
//    private String password;
//
//    private Long id;
//
//    public CustomUserDetails(User userCredential) {
//        this.email = userCredential.getEmail();
//        this.password = userCredential.getPassword();
//        this.id = userCredential.getId();
//    }
//
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return null;
//    }
//
//    @Override
//    public String getPassword() {
//        return password;
//    }
//
//    @Override
//    public String getUsername() {
//        return email;
//    }
//
//    public String getEmail() {
//        return email;
//    }
//
//    public Long getId() {
//        return id;
//    }
//
//    @Override
//    public boolean isAccountNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return true;
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return true;
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return true;
//    }
//}

public class CustomUserDetails implements UserDetails {

    private final String email;
    private final String password;
    private Long id;
    private final Set<GrantedAuthority> authorities;

    public CustomUserDetails(User userCredential) {
        this.email = userCredential.getEmail();
        this.password = userCredential.getPassword();
        this.id = userCredential.getId();

        // Convert user roles to GrantedAuthority
        this.authorities = userCredential.getRoles().stream()
                .map(role ->  new SimpleGrantedAuthority("ROLE_" + role.getName()))
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    public String getEmail() {
        return email;
    }

    public Long getId() {
        return id;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}

