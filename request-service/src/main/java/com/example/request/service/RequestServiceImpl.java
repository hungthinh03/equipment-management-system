package com.example.request.service;

import com.example.request.dto.ApiResponse;
import com.example.request.dto.CreateRequestDTO;
import com.example.request.model.Request;
import com.example.request.repository.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class RequestServiceImpl implements RequestService {
    @Autowired
    RequestRepository requestRepository;

    public Mono<ApiResponse> createRequest(CreateRequestDTO dto, String userId) {
        return requestRepository.save(new Request(dto.getUuid(), Integer.valueOf(userId), dto.getReason()))
                .map(saved -> new ApiResponse(saved.getId()));
    }
}
