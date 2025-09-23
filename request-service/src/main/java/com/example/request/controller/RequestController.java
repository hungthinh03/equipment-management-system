package com.example.request.controller;

import com.example.request.common.enums.ErrorCode;
import com.example.request.common.exception.AppException;
import com.example.request.dto.ApiResponse;
import com.example.request.dto.CreateRequestDTO;
import com.example.request.dto.RequestResponse;
import com.example.request.service.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/request")
public class RequestController {
    @Autowired
    private RequestService requestService;

    private Mono<String> validateRole(String role) {
        return Mono.just(role)
                .filter(r -> "ADMIN".equals(r) || "IT".equals(r))
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.UNAUTHORIZED)));
    }

    @PostMapping
    public Mono<ApiResponse> createRequest(@RequestBody CreateRequestDTO request,
                                           @RequestHeader("X-User-Id") String userId) {
        return requestService.createRequest(request, userId);
    }

    @GetMapping
    public Mono<RequestResponse> viewMyRequests(@RequestHeader("X-User-Id") String userId) {
        return requestService.viewMyRequests(userId);
    }
}

