package com.example.auth.service;

import com.example.auth.dto.ApiResponseDTO;
import com.example.auth.dto.LoginRequestDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface UserService {
    public Mono<List<String>> getAllUserEmails();

    //
    Mono<ApiResponseDTO> login(LoginRequestDTO request);
}
