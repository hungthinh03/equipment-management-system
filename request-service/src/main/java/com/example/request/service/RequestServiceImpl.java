package com.example.request.service;

import com.example.request.common.enums.ErrorCode;
import com.example.request.common.exception.AppException;
import com.example.request.dto.*;
import com.example.request.model.Request;
import com.example.request.repository.RequestRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class RequestServiceImpl implements RequestService {
    //@Autowired
    RequestRepository requestRepository;

    private final WebClient webClient;

    public RequestServiceImpl(RequestRepository requestRepository, WebClient.Builder webClientBuilder) {
        this.requestRepository = requestRepository;
        this.webClient = webClientBuilder.baseUrl("http://device-service").build();
    }

    public Mono<ApiResponse> createRequest(CreateRequestDTO dto, String userId, String authHeader) {
        return webClient.get()
                .uri("http://localhost:8081/device/by-uuid/{uuid}", dto.getUuid())
                .header("Authorization", authHeader)
                .retrieve()
                .onStatus(status -> status.value() == 404,
                        response -> Mono.error(new AppException(ErrorCode.NOT_FOUND))
                )
                .bodyToMono(Map.class)
                .flatMap(response -> requestRepository.save(
                        new Request(dto.getUuid(), Integer.valueOf(userId), dto.getReason())
                ))
                .map(saved -> new ApiResponse(saved.getId()));
    }

    public Mono<RequestResponse> viewMyRequests(String userId) {
        return requestRepository.findByRequesterId(Integer.valueOf(userId))
                .map(ViewRequestDTO::new)
                .collectList()
                .map(RequestResponse::new);
    }

    public Mono<PendingResponse> viewPendingRequests(String userId, String role) {
        return ("IT".equalsIgnoreCase(role) //Requests that need additional IT approval
                ? requestRepository.findByStatusAndApprovedByManagerIsNotNull("PENDING")
                : requestRepository.findByStatus("PENDING")) //Requests that admins hasn't approved
                .map(PendingRequestDTO::new)
                .collectList()
                .map(PendingResponse::new);
    }
}
