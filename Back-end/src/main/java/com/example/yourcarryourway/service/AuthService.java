package com.example.yourcarryourway.service;

import com.example.yourcarryourway.domain.entities.User;
import com.example.yourcarryourway.domain.enums.UserRole;
import com.example.yourcarryourway.dto.AuthResponse;
import com.example.yourcarryourway.dto.LoginRequest;
import com.example.yourcarryourway.dto.RegisterRequest;
import com.example.yourcarryourway.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Locale;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, JwtService jwtService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse login(LoginRequest request) throws Exception {
        Optional<User> user = userRepository.findByEmail(request.getEmail());
        
        if (user.isEmpty()) {
            throw new Exception("User not found");
        }

        User foundUser = user.get();
        if (!passwordEncoder.matches(request.getPassword(), foundUser.getPassword())) {
            throw new Exception("Invalid password");
        }

        String token = jwtService.generateToken(foundUser.getId(), foundUser.getEmail());
        return new AuthResponse(token, foundUser.getId(), foundUser.getEmail(),
                foundUser.getNom(), foundUser.getPrenom(), foundUser.getRole().name());
    }

    public AuthResponse register(RegisterRequest request) throws Exception {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new Exception("Email already exists");
        }

        UserRole role = parseRole(request.getRole());
        User user = new User(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getNom(),
                request.getPrenom(),
                role
        );

        User savedUser = userRepository.save(user);
        String token = jwtService.generateToken(savedUser.getId(), savedUser.getEmail());
        
        return new AuthResponse(token, savedUser.getId(), savedUser.getEmail(),
                savedUser.getNom(), savedUser.getPrenom(), savedUser.getRole().name());
    }

    private UserRole parseRole(String requestedRole) throws Exception {
        if (requestedRole == null || requestedRole.isBlank()) {
            return UserRole.CLIENT;
        }

        try {
            return UserRole.valueOf(requestedRole.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new Exception("Invalid role: " + requestedRole);
        }
    }
}
