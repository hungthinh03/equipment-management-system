package com.example.auth.service;

import com.example.auth.dto.ApiResponseDTO;
import com.example.auth.dto.LoginRequestDTO;
import reactor.core.publisher.Mono;

public interface UserService {
    Mono<ApiResponseDTO> login(LoginRequestDTO request);
}
