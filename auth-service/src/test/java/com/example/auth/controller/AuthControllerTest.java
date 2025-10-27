package com.example.auth.controller;

import com.example.auth.common.enums.ErrorCode;
import com.example.auth.dto.ApiResponseDTO;
import com.example.auth.dto.LoginRequestDTO;
import com.example.auth.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@WebFluxTest(Controller.class)
@Import(AuthControllerTest.TestConfig.class)
class AuthControllerTest {
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private UserService userService; // Spring-managed mock

    @TestConfiguration
    static class TestConfig {
        @Bean
        UserService userService() {
            return mock(UserService.class); // Mockito mock
        }
    }

    @Test
    void login_success() {
        LoginRequestDTO dto = new LoginRequestDTO("admin1@example.com", "12345678");
        String token = "my-token";

        // Mock service response
        when(userService.login(any(LoginRequestDTO.class)))
                .thenReturn(Mono.just(new ApiResponseDTO(token)));

        // Perform POST request
        webTestClient.post()
                .uri("/auth/login")
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ApiResponseDTO.class)
                .value(response -> {
                    assertEquals("success", response.getStatus());
                    assertEquals(token, response.getToken());
                });
    }

    @Test
    void login_fail() {
        LoginRequestDTO dto = new LoginRequestDTO();
        String token = "my-token";

        // Mock service response
        when(userService.login(any(LoginRequestDTO.class)))
                .thenReturn(Mono.just(new ApiResponseDTO(token)));

        // Perform POST request
        webTestClient.post()
                .uri("/auth/login")
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ApiResponseDTO.class)
                .value(response -> {
                    assertEquals("success", response.getStatus());
                    assertEquals(token, response.getToken());
                });
    }

    @Test
    void login_fail_invalidInput() {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("admin1@example.com");

        when(userService.login(any(LoginRequestDTO.class)))
                .thenReturn(Mono.just(new ApiResponseDTO(ErrorCode.INVALID_INPUT)));

        webTestClient.post()
                .uri("/auth/login")
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ApiResponseDTO.class)
                .value(res -> assertEquals(ErrorCode.INVALID_INPUT.getCode(), res.getStatusCode()));
    }

    @Test
    void login_fail_invalidInfo() {
        LoginRequestDTO dto = new LoginRequestDTO();
        dto.setEmail("wrong@example.com");
        dto.setPassword("wrongpass");

        when(userService.login(any(LoginRequestDTO.class)))
                .thenReturn(Mono.just(new ApiResponseDTO(ErrorCode.INVALID_INFO)));

        webTestClient.post()
                .uri("/auth/login")
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ApiResponseDTO.class)
                .value(res -> assertEquals(ErrorCode.INVALID_INFO.getCode(), res.getStatusCode()));
    }


}