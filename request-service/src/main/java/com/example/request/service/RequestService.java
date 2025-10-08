package com.example.request.service;

import com.example.request.dto.*;
import reactor.core.publisher.Mono;

public interface RequestService {
    Mono<ApiResponse> createRequest(CreateRequestDTO request, String userId, String authHeader);

    Mono<MyRequestResponse> viewMyRequests(String userId);

    Mono<RequestResponse> viewAllPendingRequests(String userId, String role);

    Mono<RequestResponse> viewPendingRequest(Integer id, String userId, String role);

    Mono<ApiResponse> resolveRequest(ResolveRequestDTO request, Integer id, String userId, String role, String authHeader);

    Mono<RequestResponse> viewAllPendingAssignments(String role);

    Mono<ApiResponse> confirmDeviceAssignment(Integer id, String role, String authHeader);

    Mono<ApiResponse> submitReturnNotice(Integer id, String userId);

    Mono<RequestResponse> viewAllReturnNotices(String userId, String role);

    Mono<RequestResponse> viewReturnNotice(Integer id, String userId, String role);

    Mono<ApiResponse> confirmReturnNotice(Integer id, String userId, String role, String authHeader);

    Mono<RequestResponse> viewMyProcessedRequests(String userId);


}
