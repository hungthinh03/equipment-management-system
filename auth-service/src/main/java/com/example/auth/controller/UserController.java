package com.example.auth.controller;

import com.example.auth.dto.ApiResponseDTO;
import com.example.auth.dto.LoginRequestDTO;
import com.example.auth.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/emails")
    public Mono<List<String>>  getAllEmails() {
        return userService.getAllUserEmails();
    }


    @PostMapping("/login")
    public Mono<ApiResponseDTO> login(@RequestBody LoginRequestDTO request) {
        return userService.login(request);
    }
}
