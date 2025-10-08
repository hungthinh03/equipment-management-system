package com.example.request.controller;

import com.example.request.common.enums.ErrorCode;
import com.example.request.common.exception.AppException;
import com.example.request.dto.*;
import com.example.request.service.RequestService;
import com.example.request.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/request")
public class RequestController {
    @Autowired
    private RequestService requestService;

    @Autowired
    private JwtUtil jwtUtil;

    private Mono<String> validateRole(String role) {
        return Mono.just(role)
                .filter(r -> "ADMIN".equals(r) || "IT".equals(r))
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.UNAUTHORIZED)));
    }

    @PostMapping
    public Mono<ApiResponse> createRequest(@RequestBody CreateRequestDTO request,
                                           @RequestHeader("X-User-Id") String userId,
                                           @RequestHeader("Authorization") String authHeader) {
        return requestService.createRequest(request, userId, authHeader); //more powerful manager
    }

    @GetMapping
    public Mono<MyRequestResponse> viewMyRequests(@RequestHeader("X-User-Id") String userId) {
        return requestService.viewMyRequests(userId);
    }

    @GetMapping("/pending")
    public Mono<RequestResponse> viewAllPendingRequests(@RequestHeader("X-User-Id") String userId,
                                                        @RequestHeader("X-User-Role") String role) {
        return validateRole(role)
                .flatMap(r -> requestService.viewAllPendingRequests(userId, r));
    }

    @GetMapping("/pending/{id}")
    public Mono<RequestResponse> viewPendingRequest(@PathVariable Integer id,
                                                    @RequestHeader("X-User-Id") String userId,
                                                    @RequestHeader("X-User-Role") String role) {
        return validateRole(role)
                .flatMap(r -> requestService.viewPendingRequest(id, userId, r));
    }

    @PostMapping("/pending/{id}")
    public Mono<ApiResponse> resolveRequest(@RequestBody ResolveRequestDTO request,
                                            @PathVariable Integer id,
                                            @RequestHeader("X-User-Id") String userId,
                                            @RequestHeader("X-User-Role") String role,
                                            @RequestHeader("Authorization") String authHeader) {
        return validateRole(role)
                .flatMap(r -> requestService.resolveRequest(request, id, userId, r, authHeader));
    }

    @GetMapping("/assign")
    public Mono<RequestResponse> viewAllPendingAssignments(@RequestHeader("X-User-Role") String role) {
        return validateRole(role)
                .flatMap(r -> requestService.viewAllPendingAssignments(r));
    }

    @PostMapping("/assign/{id}")
    public Mono<ApiResponse> confirmDeviceAssignment(@PathVariable Integer id,
                                                     @RequestHeader("X-User-Role") String role,
                                                     @RequestHeader("Authorization") String authHeader) {
        return validateRole(role)
                .flatMap(r -> requestService.confirmDeviceAssignment(id, r, authHeader));
    }

    @PostMapping("/return/{id}")
    public Mono<ApiResponse> submitReturnNotice(@PathVariable Integer id,
                                                @RequestHeader("X-User-Id") String userId) {
        return requestService.submitReturnNotice(id, userId);
    }

    @GetMapping("/return")
    public Mono<RequestResponse> viewAllReturnNotices(@RequestHeader("X-User-Id") String userId,
                                                         @RequestHeader("X-User-Role") String role) {
        return validateRole(role)
                .flatMap(r -> requestService.viewAllReturnNotices(userId, r));
    }

    @GetMapping("/return/{id}")
    public Mono<RequestResponse> viewReturnNotice(@PathVariable Integer id,
                                                     @RequestHeader("X-User-Id") String userId,
                                                     @RequestHeader("X-User-Role") String role) {
        return validateRole(role)
                .flatMap(r -> requestService.viewReturnNotice(id, userId, r));
    }

    @PostMapping("/return/{id}/confirm")
    public Mono<ApiResponse> confirmReturnNotice(@PathVariable Integer id,
                                          @RequestHeader("X-User-Id") String userId,
                                          @RequestHeader("X-User-Role") String role,
                                          @RequestHeader("Authorization") String authHeader) {
        return validateRole(role)
                .flatMap(r -> requestService.confirmReturnNotice(id, userId, r, authHeader));
    }

    @GetMapping("/processed")
    public Mono<RequestResponse> viewMyProcessedRequests(@RequestHeader("X-User-Id") String userId,
                                                     @RequestHeader("X-User-Role") String role) {
        return validateRole(role)
                .flatMap(r -> requestService.viewMyProcessedRequests(userId));
    }
}

