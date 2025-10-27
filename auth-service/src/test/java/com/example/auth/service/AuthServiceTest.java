package com.example.auth.service;

import com.example.auth.common.enums.ErrorCode;
import com.example.auth.common.enums.UserRole;
import com.example.auth.dto.LoginRequestDTO;
import com.example.auth.model.User;
import com.example.auth.repository.UserRepository;
import com.example.auth.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private UserRepository userRepo;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void login_success() {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("admin@example.com");
        dto.setPassword("12345678");

        String hashed = new BCryptPasswordEncoder().encode(dto.getPassword());

        User user = new User();
        user.setId(1);
        user.setEmail(dto.getEmail());
        user.setPassword(hashed);
        user.setRole(UserRole.ADMIN);

        String token = "my-token";

        when(userRepo.findByEmail(dto.getEmail())).thenReturn(Mono.just(user));
        when(jwtUtil.generateToken(user.getId(), user.getRole().toString()))
                .thenReturn(token);

        StepVerifier.create(userService.login(dto))
                .expectNextMatches(res ->
                        token.equals(res.getToken()) &&
                        "success".equals(res.getStatus()))
                .verifyComplete();
    }

    @Test
    void login_fail_invalidInput() {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("");
        dto.setPassword(null);

        StepVerifier.create(userService.login(dto))
                .expectNextMatches(res ->
                        res.getStatusCode() == ErrorCode.INVALID_INPUT.getCode()
                        && "error".equals(res.getStatus()))
                .verifyComplete();
    }

    @Test
    void login_fail_invalidInfo() {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("missing@example.com");
        dto.setPassword("wrong password");

        when(userRepo.findByEmail(dto.getEmail())).thenReturn(Mono.empty());

        StepVerifier.create(userService.login(dto))
                .expectNextMatches(res ->
                        res.getStatusCode() == ErrorCode.INVALID_INFO.getCode()
                                && "error".equals(res.getStatus()))
                .verifyComplete();
    }


}