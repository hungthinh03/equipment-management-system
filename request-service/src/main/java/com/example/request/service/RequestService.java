package com.example.request.service;

import com.example.request.dto.ApiResponse;
import com.example.request.dto.CreateRequestDTO;
import com.example.request.dto.RequestResponse;
import reactor.core.publisher.Mono;

public interface RequestService {
    Mono<ApiResponse> createRequest(CreateRequestDTO request, String userId);

    Mono<RequestResponse> viewMyRequests(String userId);
}
