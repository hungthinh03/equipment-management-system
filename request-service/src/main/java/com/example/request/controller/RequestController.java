package com.example.request.controller;

import com.example.request.annotation.RequireRole;
import com.example.request.dto.*;
import com.example.request.response.ApiResponse;
import com.example.request.response.MyRequestResponse;
import com.example.request.response.RequestResponse;
import com.example.request.service.RequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/request")
public class RequestController {
    @Autowired
    private RequestService requestService;


    @PostMapping
    public Mono<ApiResponse> createRequest(@RequestBody CreateRequestDTO request,
                                           @RequestHeader("X-User-Id") String userId,
                                           @RequestHeader("Authorization") String authHeader) {
        return requestService.createRequest(request, userId, authHeader);
    }

    @GetMapping
    public Mono<MyRequestResponse> viewAllMyRequests(@RequestHeader("X-User-Id") String userId) {
        return requestService.viewAllMyRequests(userId);
    }

    @GetMapping("/{id}")
    public Mono<MyRequestResponse> viewMyRequest(@PathVariable Integer id,
                                                 @RequestHeader("X-User-Id") String userId) {
        return requestService.viewMyRequest(id, userId);
    }

    @PostMapping("/{id}/cancel")
    public Mono<ApiResponse> cancelMyRequest(@PathVariable Integer id,
                                             @RequestHeader("X-User-Id") String userId) {
        return requestService.cancelMyRequest(id, userId);
    }

    @GetMapping("/pending")
    @RequireRole({"ADMIN", "IT"})
    public Mono<RequestResponse> viewAllPendingRequests(@RequestHeader("X-User-Id") String userId,
                                                        @RequestHeader("X-User-Role") String role) {
        return requestService.viewAllPendingRequests(userId, role);
    }

    @GetMapping("/pending/{id}")
    @RequireRole({"ADMIN", "IT"})
    public Mono<RequestResponse> viewPendingRequest(@PathVariable Integer id,
                                                    @RequestHeader("X-User-Id") String userId,
                                                    @RequestHeader("X-User-Role") String role) {
        return  requestService.viewPendingRequest(id, userId, role);
    }

    @PostMapping("/pending/{id}")
    @RequireRole({"ADMIN", "IT"})
    public Mono<ApiResponse> resolveRequest(@RequestBody ResolveRequestDTO request,
                                            @PathVariable Integer id,
                                            @RequestHeader("X-User-Id") String userId,
                                            @RequestHeader("X-User-Role") String role,
                                            @RequestHeader("Authorization") String authHeader) {
        return requestService.resolveRequest(request, id, userId, role, authHeader);
    }

    @GetMapping("/assign")
    @RequireRole({"ADMIN", "IT"})
    public Mono<RequestResponse> viewAllPendingAssignments(@RequestHeader("X-User-Role") String role) {
        return requestService.viewAllPendingAssignments(role);
    }

    @PostMapping("/assign/{id}")
    @RequireRole({"ADMIN", "IT"})
    public Mono<ApiResponse> confirmDeviceAssignment(@PathVariable Integer id,
                                                     @RequestHeader("X-User-Id") String userId,
                                                     @RequestHeader("X-User-Role") String role,
                                                     @RequestHeader("Authorization") String authHeader) {
        return requestService.confirmDeviceAssignment(id, userId, role, authHeader);
    }

    @PostMapping("/return/{id}")
    public Mono<ApiResponse> submitReturnNotice(@PathVariable Integer id,
                                                @RequestHeader("X-User-Id") String userId) {
        return requestService.submitReturnNotice(id, userId);
    }

    @GetMapping("/return")
    @RequireRole({"ADMIN", "IT"})
    public Mono<RequestResponse> viewAllReturnNotices(@RequestHeader("X-User-Id") String userId,
                                                         @RequestHeader("X-User-Role") String role) {
        return  requestService.viewAllReturnNotices(userId, role);
    }

    @GetMapping("/return/{id}")
    @RequireRole({"ADMIN", "IT"})
    public Mono<RequestResponse> viewReturnNotice(@PathVariable Integer id,
                                                     @RequestHeader("X-User-Id") String userId,
                                                     @RequestHeader("X-User-Role") String role) {
        return requestService.viewReturnNotice(id, userId, role);
    }

    @PostMapping("/return/{id}/confirm")
    @RequireRole({"ADMIN", "IT"})
    public Mono<ApiResponse> confirmReturnNotice(@PathVariable Integer id,
                                                 @RequestHeader("X-User-Id") String userId,
                                                 @RequestHeader("X-User-Role") String role,
                                                 @RequestHeader("Authorization") String authHeader) {
        return requestService.confirmReturnNotice(id, userId, role, authHeader);
    }

    @GetMapping("/processed")
    @RequireRole({"ADMIN", "IT"}) // needs @RequestHeader("X-User-Role")
    public Mono<RequestResponse> viewMyProcessedRequests(@RequestHeader("X-User-Id") String userId,
                                                         @RequestHeader("X-User-Role") String role) {
        return requestService.viewMyProcessedRequests(userId);
    }

    @PostMapping("/register")
    public Mono<ApiResponse> createRegistry(@RequestBody CreateRegistryDTO registry,
                                            @RequestHeader("X-User-Id") String userId,
                                            @RequestHeader("Authorization") String authHeader) {
        return requestService.createRegistry(registry, userId, authHeader);
    }
}

