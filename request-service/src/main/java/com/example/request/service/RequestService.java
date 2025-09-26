package com.example.request.service;

import com.example.request.dto.*;
import reactor.core.publisher.Mono;

public interface RequestService {
    Mono<ApiResponse> createRequest(CreateRequestDTO request, String userId, String authHeader);

    Mono<RequestResponse> viewMyRequests(String userId);

    Mono<PendingResponse> viewAllPendingRequests(String userId, String role);

    Mono<PendingResponse> viewPendingRequest(Integer id, String userId, String role);

    Mono<ApiResponse> resolveRequest(ResolveRequestDTO request, Integer id, String userId, String role, String authHeader);
}
