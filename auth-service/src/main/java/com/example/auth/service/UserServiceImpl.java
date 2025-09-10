package com.example.auth.service;

import com.example.auth.common.enums.ErrorCode;
import com.example.auth.common.exception.AppException;
import com.example.auth.dto.ApiResponseDTO;
import com.example.auth.dto.LoginRequestDTO;
import com.example.auth.repository.UserRepository;
import com.example.auth.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private JwtUtil jwtUtil;

    public Mono<ApiResponseDTO> login(LoginRequestDTO request) {
        return Mono.justOrEmpty(request)
                .filter(req -> Objects.nonNull(req.getEmail()) && Objects.nonNull(req.getPassword()))
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.INVALID_INPUT)))
                .flatMap(req -> userRepo.findByEmail(req.getEmail())
                        .filter(user -> passwordMatches(req.getPassword(), user.getPassword()))
                        .switchIfEmpty(Mono.error(new AppException(ErrorCode.INVALID_INFO)))
                        .map(user -> {
                            String token = jwtUtil.generateToken(user.getEmail());
                            return new ApiResponseDTO(token);
                        })
                )
                .onErrorResume(AppException.class, e ->
                        Mono.just(new ApiResponseDTO(e.getErrorCode()))
                );
    }

    private boolean passwordMatches(String rawPassword, String hashedPassword) {
        return new BCryptPasswordEncoder().matches(rawPassword, hashedPassword);
    }
}
