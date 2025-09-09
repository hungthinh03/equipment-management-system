package com.example.auth.service;

import com.example.auth.dto.ApiResponseDTO;
import com.example.auth.dto.LoginRequestDTO;
import com.example.auth.repository.UserRepository;
import com.example.auth.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepo;
    private JwtUtil jwtUtil;

    public Mono<ApiResponseDTO> login(LoginRequestDTO request) {
        return userRepo.findByEmail(request.getEmail())
                .filter(user -> passwordMatches(request.getPassword(), user.getPassword()))
                .switchIfEmpty(Mono.error(new RuntimeException("Email or password is incorrect")))
                .map(user -> {
                    String token = jwtUtil.generateToken(user.getEmail());
                    return new ApiResponseDTO(token);
                });
    }

    private boolean passwordMatches(String rawPassword, String hashedPassword) {
        return new BCryptPasswordEncoder().matches(rawPassword, hashedPassword);
    }
}
