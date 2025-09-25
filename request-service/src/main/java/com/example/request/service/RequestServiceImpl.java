package com.example.request.service;

import com.example.request.common.enums.ErrorCode;
import com.example.request.common.exception.AppException;
import com.example.request.dto.*;
import com.example.request.model.Request;
import com.example.request.repository.RequestRepository;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class RequestServiceImpl implements RequestService {
    //@Autowired
    RequestRepository requestRepository;

    private final WebClient webClient;

    public RequestServiceImpl(RequestRepository requestRepository, WebClient.Builder webClientBuilder) {
        this.requestRepository = requestRepository;
        this.webClient = webClientBuilder.baseUrl("http://device-service").build();
    }

    private Mono<DeviceStatusDTO> getDeviceByUuid(UUID uuid, String authHeader) {
        return webClient.get()
                .uri("http://localhost:8081/device/by-uuid/{uuid}", uuid)
                .header("Authorization", authHeader)
                .retrieve()
                .onStatus(status -> status.value() == 404,
                        response -> Mono.error(new AppException(ErrorCode.NOT_FOUND)))
                .bodyToMono(new ParameterizedTypeReference<Map<String, DeviceStatusDTO>>() {})
                .map(map -> map.get("result"));
                //.flatMap(Mono::just);
    }

    public Mono<ApiResponse> createRequest(CreateRequestDTO dto, String userId, String authHeader) {
        return getDeviceByUuid(dto.getUuid(), authHeader) // check device exists
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

    public Mono<PendingResponse> viewAllPendingRequests(String role) {
        return ("IT".equalsIgnoreCase(role) //Requests that need additional IT approval
                ? requestRepository.findByStatusAndApprovedByManagerIsNotNull("PENDING")
                : requestRepository.findByStatus("PENDING")) //Requests that admins hasn't approved
                .map(PendingRequestDTO::new)
                .collectList()
                .map(PendingResponse::new);
    }

    private boolean canAccessRequest(Request request, String role) {
        if ("IT".equals(role)) {
            return "PENDING".equals(request.getStatus()) && request.getApprovedByManager() != null;
        }
        else { //Admin
            return "PENDING".equals(request.getStatus()) && request.getApprovedByManager() == null;
        }
    }

    public Mono<PendingResponse> viewPendingRequest(Integer id, String role) {
        return requestRepository.findById(id)
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.NOT_FOUND)))
                .filter(request -> canAccessRequest(request, role))
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.INACCESSIBLE)))
                .map(request -> new PendingResponse(List.of(new PendingRequestDTO(request))));
    }

    private void applyInfoAdmin(Request request, String userId, String comment, Instant now) {
        request.setApprovedByManager(Integer.valueOf(userId));
        request.setManagerApprovedAt(now);
        request.setManagerComment(comment);
    }

    private void applyInfoIt(Request request, String userId, String comment, Instant now) {
        request.setApprovedByIt(Integer.valueOf(userId));
        request.setItApprovedAt(now);
        request.setItComment(comment);
    }

    private Mono<Request> approveRequest(Request request, String role, String userId, String comment, String authHeader) {
        return Mono.defer(() -> {
            if ("Admin".equals(role)) {
                return getDeviceByUuid(request.getDeviceUuid(), authHeader)
                        .flatMap(device -> {
                            if (!"AVAILABLE".equals(device.getStatus())) {
                                return Mono.error(new AppException(ErrorCode.DEVICE_IN_USE));
                            }
                            if ("GENERAL".equalsIgnoreCase(device.getCategory())) {
                                request.setStatus("APPROVED"); // IT approval not needed
                            }
                            applyInfoAdmin(request, userId, comment, Instant.now()); //sign request with admin info
                            return Mono.just(request); // stays PENDING for IT if not GENERAL
                        });
            }
            applyInfoIt(request, userId, comment, Instant.now());
            request.setStatus("APPROVED");
            return Mono.just(request);
        });
    }

    private Mono<Request> denyRequest(Request request, String role, String userId, String comment) {
        return Mono.defer(() -> {
            request.setStatus("DENIED");
            if ("Admin".equals(role))
                applyInfoAdmin(request, userId, comment, Instant.now());
            else
                applyInfoIt(request, userId, comment, Instant.now());
            return Mono.just(request);
        });
    }

    public Mono<ApiResponse> resolveRequest(ResolveRequestDTO dto, Integer id, String userId, String role, String authHeader) {
        return requestRepository.findById(id)
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.NOT_FOUND)))
                .filter(request -> canAccessRequest(request, role))
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.UNAUTHORIZED)))
                .flatMap(request -> dto.isApprove()
                        ? approveRequest(request, role, userId, dto.getComment(), authHeader)
                        : denyRequest(request, role, userId, dto.getComment()))
                .flatMap(requestRepository::save)
                .map(saved -> new ApiResponse(saved.getId()));
    }

}
