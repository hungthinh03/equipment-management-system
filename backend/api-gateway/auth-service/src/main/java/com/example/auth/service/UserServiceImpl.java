package com.example.auth.service;

import com.example.auth.common.enums.ErrorCode;
import com.example.auth.common.exception.AppException;
import com.example.auth.dto.ApiResponseDTO;
import com.example.auth.dto.LoginRequestDTO;
import com.example.auth.model.User;
import com.example.auth.repository.UserRepository;
import com.example.auth.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private JwtUtil jwtUtil;

    public Mono<List<String>> getAllUserEmails() {
        return userRepo.findAll()
                .map(User::getEmail).collectList(); //user -> user.getEmail()
    }
    //

    private Mono<LoginRequestDTO> validateLoginDTO(LoginRequestDTO req) {
        return Mono.justOrEmpty(req)
                .filter(r -> Stream.of(r.getEmail(), r.getPassword())
                        .allMatch(s -> Objects.nonNull(s) && !s.isBlank()))
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.INVALID_INPUT)));
    }

    public Mono<ApiResponseDTO> login(LoginRequestDTO request) {
        return validateLoginDTO(request)
                .flatMap(req -> userRepo.findByEmail(req.getEmail())
                        .filter(user -> passwordMatches(req.getPassword(), user.getPassword()))
                        .switchIfEmpty(Mono.error(new AppException(ErrorCode.INVALID_INFO)))
                        .map(user -> {
                            String token = jwtUtil.generateToken(user.getId(), user.getRole().toString());
                            return new ApiResponseDTO(token);
                        })
                )
                .onErrorResume(AppException.class,
                        e -> Mono.just(new ApiResponseDTO(e.getErrorCode())));
    }


    private boolean passwordMatches(String rawPassword, String hashedPassword) {
        return new BCryptPasswordEncoder().matches(rawPassword, hashedPassword);
    }
}
