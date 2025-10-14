package com.example.request.service;

import com.example.request.common.enums.ErrorCode;
import com.example.request.common.exception.AppException;
import com.example.request.dto.*;
import com.example.request.model.Registry;
import com.example.request.model.Request;
import com.example.request.repository.RegistryRepository;
import com.example.request.repository.RequestRepository;
import com.example.request.response.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class RequestServiceImpl implements RequestService {
    RequestRepository requestRepository;
    RegistryRepository registryRepository;

    private final WebClient webClient;

    public RequestServiceImpl(RequestRepository requestRepository, RegistryRepository registryRepository, WebClient.Builder webClientBuilder) {
        this.requestRepository = requestRepository;
        this.registryRepository = registryRepository;
        this.webClient = webClientBuilder.baseUrl("http://device-service").build();
    }

    private Mono<UUID> validateUuid(String uuid) {
        try {
            return Mono.just(UUID.fromString(uuid));
        } catch (IllegalArgumentException e) {
            return Mono.error(new AppException(ErrorCode.INVALID_UUID));
        }
    }

    private Mono<DeviceStatusDTO> getDeviceByUuid(String uuid, String authHeader) {
        return validateUuid(uuid)
                .flatMap(validUuid ->
                        webClient.get() //web request service
                                .uri("http://localhost:8081/device/by-uuid/{uuid}", validUuid)
                                .header("Authorization", authHeader)
                                .header("X-Service-Source", "request-service")
                                .retrieve()
                                .onStatus(status -> status.value() == 404,
                                        response ->
                                                Mono.error(new AppException(ErrorCode.DEVICE_NOT_FOUND)))
                                .bodyToMono(DeviceResponse.class)
                                .map(DeviceResponse::getResult));
    }

    public Mono<ApiResponse> createRequest(CreateRequestDTO dto, String userId, String authHeader) {
        return getDeviceByUuid(dto.getUuid(), authHeader)
                .filter(device -> "AVAILABLE".equalsIgnoreCase(device.getStatus()))
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.DEVICE_UNAVAILABLE)))
                .flatMap(device -> requestRepository.save(
                        new Request("ASSIGN", dto.getUuid(), Integer.valueOf(userId), dto.getReason())
                ))
                .map(saved -> new ApiResponse(saved.getId()));
    }

    public Mono<MyRequestResponse> viewAllMyRequests(String userId) {
        return requestRepository.findByRequesterId(Integer.valueOf(userId))
                .filter(request -> "ASSIGN".equals(request.getRequestType())) // exclude registries
                .map(ViewMyRequestDTO::new)
                .collectList()
                .map(MyRequestResponse::new);
    }

    public Mono<MyRequestResponse> viewMyRequest(Integer id, String userId) {
        return requestRepository.findById(id)
                .filter(request -> "ASSIGN".equals(request.getRequestType()))
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.NOT_FOUND)))
                .filter(request -> request.getRequesterId().equals(Integer.valueOf(userId)))
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.UNAUTHORIZED)))
                .map(ViewMyRequestDTO::new)
                .map(request -> new MyRequestResponse(List.of(request)));
    }

    public Mono<ApiResponse> cancelMyRequest(Integer id, String userId) {
        return requestRepository.findById(id)
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.NOT_FOUND)))
                .filter(request -> request.getRequesterId().equals(Integer.valueOf(userId)))
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.UNAUTHORIZED)))
                .filter(request -> "PENDING".equalsIgnoreCase(request.getStatus()))
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.INVALID_OPERATION)))
                .flatMap(request -> {
                    request.setStatus("CANCELLED");
                    return requestRepository.save(request);
                })
                .map(saved -> new ApiResponse(saved.getId()));
    }

    public Mono<RequestResponse> viewAllPendingRequests(String userId, String role) {
        return ("IT".equalsIgnoreCase(role) //Requests that need additional IT approval
                ? registryRepository.findAllPendingRequestsForIT()
                : registryRepository.findAllPendingRequestsForManager()) //Requests admins hasn't approved
                .filter(request -> !request.getRequesterId().equals(Integer.valueOf(userId)))
                .map(dto -> "REGISTER".equalsIgnoreCase(dto.getRequestType())
                        ? new RegisterRequestDTO(dto)
                        : new AssignRequestDTO(dto))
                .collectList()
                .map(RequestResponse::new);
    }


    private boolean canAccessPendingRequest(Request request, String role) {
        if ("IT".equalsIgnoreCase(role)) {
            return "PENDING".equalsIgnoreCase(request.getStatus()) && request.getProcessedByManager() != null;
        }
        else { //Admin
            return "PENDING".equalsIgnoreCase(request.getStatus()) && request.getProcessedByManager() == null;
        }
    }

    public Mono<RequestResponse> viewPendingRequest(Integer id, String userId, String role) {
        return registryRepository.findRequestById(id)
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.NOT_FOUND)))
                .filter(dto -> canAccessPendingRequest(new Request(dto), role)) // also exclude own requests
                .filter(dto -> !dto.getRequesterId().equals(Integer.valueOf(userId)))
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.INVALID_OPERATION)))
                .map(dto -> "REGISTER".equalsIgnoreCase(dto.getRequestType())
                        ? new RegisterRequestDTO(dto)
                        : new AssignRequestDTO(dto))
                .map(dto -> new RequestResponse(List.of(dto)));
    }

    private void applyInfoAdmin(Request request, String userId, String comment, Instant now) {
        request.setProcessedByManager(Integer.valueOf(userId));
        request.setManagerProcessedAt(now);
        request.setManagerComment(comment);
    }

    private void applyInfoIt(Request request, String userId, String comment, Instant now) {
        request.setProcessedByIt(Integer.valueOf(userId));
        request.setItProcessedAt(now);
        request.setItComment(comment);
    }

    private Mono<Request> updateDeviceAssignment(Request request, UpdateAssignmentDTO dto, String authHeader) {
        return webClient.put()
                .uri("http://localhost:8081/device/by-uuid/{uuid}", request.getDeviceUuid())
                .header("Authorization", authHeader)
                .header("X-Service-Source", "request-service")
                .bodyValue(dto)
                .retrieve()
                .toBodilessEntity()
                .thenReturn(request);
    }

    private Mono<Request> approveRequest(Request request, String role, String userId,
                                         String comment, String authHeader) {
        return Mono.defer(() -> {
            if ("ADMIN".equalsIgnoreCase(role)) {
                return getDeviceByUuid(request.getDeviceUuid().toString(), authHeader)
                        .flatMap(device -> {
                            if (!"AVAILABLE".equalsIgnoreCase(device.getStatus())) {
                                return Mono.error(new AppException(ErrorCode.DEVICE_UNAVAILABLE));
                            }
                            if ("GENERAL".equalsIgnoreCase(device.getCategory())) {
                                applyInfoAdmin(request, userId, comment, Instant.now()); //sign request with admin info
                                request.setStatus("APPROVED"); // when IT approval not needed
                                return updateDeviceAssignment(
                                        request,
                                        new UpdateAssignmentDTO("RESERVED", request.getRequesterId()),
                                        authHeader);
                            }
                            applyInfoAdmin(request, userId, comment, Instant.now());
                            return Mono.just(request); // else stays PENDING for IT approval
                        });
            } // IT
            applyInfoIt(request, userId, comment, Instant.now());
            request.setStatus("APPROVED");
            return updateDeviceAssignment(request,
                    new UpdateAssignmentDTO("RESERVED", request.getRequesterId()),
                    authHeader);
        });
    }

    private Mono<Request> denyRequest(Request request, String role, String userId, String comment) {
        return Mono.just(request)
                .map(req -> {
                    if ("ADMIN".equalsIgnoreCase(role)) {
                        applyInfoAdmin(req, userId, comment, Instant.now());
                    } else {
                        applyInfoIt(req, userId, comment, Instant.now());
                    }
                    req.setStatus("REJECTED");
                    return req;
                });
    }

    public Mono<ApiResponse> resolveRequest(ResolveRequestDTO dto, Integer id,
                                            String userId, String role, String authHeader) {
        return requestRepository.findById(id)
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.NOT_FOUND)))
                .filter(request -> canAccessPendingRequest(request, role))
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.INVALID_OPERATION)))
                .flatMap(request -> dto.isApprove()
                        ? approveRequest(request, role, userId, dto.getComment(), authHeader)
                        : denyRequest(request, role, userId, dto.getComment()))
                .flatMap(requestRepository::save)
                .map(saved -> new ApiResponse(saved.getId()));
    }

    private boolean canConfirmAssignment(Request request, String role) {
        if ("IT".equalsIgnoreCase(role)) {
            return "APPROVED".equalsIgnoreCase(request.getStatus()) && request.getProcessedByIt() != null;
        }
        else {
            return "APPROVED".equalsIgnoreCase(request.getStatus()) && request.getProcessedByIt() == null;
        }
    }

    public Mono<RequestResponse> viewAllPendingAssignments(String role) {
        return requestRepository.findAllByStatus("APPROVED")
                .filter(request -> "ASSIGN".equals(request.getRequestType()))
                .filter(request -> canConfirmAssignment(request, role))
                .map(AssignRequestDTO::new)
                .collectList()
                .map(RequestResponse::new);
    }

    public Mono<ApiResponse> confirmDeviceAssignment(Integer id, String userId, String role, String authHeader) {
        return requestRepository.findById(id)
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.NOT_FOUND)))
                .filter(request -> canConfirmAssignment(request, role))
                .filter(request -> "ASSIGN".equals(request.getRequestType()))
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.INVALID_OPERATION)))
                .flatMap(request -> {
                    request.setStatus("DELIVERED");
                    request.setDeliveredAt(Instant.now());
                    request.setDeliveredBy(Integer.valueOf(userId));
                    return updateDeviceAssignment(
                            request,
                            new UpdateAssignmentDTO("ASSIGNED", request.getRequesterId()),
                            authHeader);
                })
                .flatMap(requestRepository::save)
                .map(saved -> new ApiResponse(saved.getId()));
    }

    public Mono<ApiResponse> submitReturnNotice(Integer id, String userId) {
        return requestRepository.findById(id)
                .filter(req -> req.getRequesterId().equals(Integer.valueOf(userId)))
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.UNAUTHORIZED))) // Can only submit for own requests
                .filter(req -> "DELIVERED".equalsIgnoreCase(req.getStatus()))
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.INVALID_OPERATION)))
                .filter(req -> req.getReturnSubmittedAt() == null)
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.ALREADY_REQUESTED_CLOSE)))
                .flatMap(req -> {
                    req.setReturnSubmittedAt(Instant.now());
                    return requestRepository.save(req);
                })
                .map(req -> new ApiResponse(req.getId()));
    }

    private boolean canCloseRequest(Request request, String role) {
        if ("ADMIN".equalsIgnoreCase(role)) {
            return request.getProcessedByIt() == null; // General requests only
        } //IT
        return request.getProcessedByIt() != null; // Network requests
    }

    public Mono<RequestResponse> viewAllReturnNotices(String userId, String role) {
        return requestRepository.findByReturnSubmittedAtIsNotNull()
                .filter(req -> "DELIVERED".equalsIgnoreCase(req.getStatus()))
                .filter(req -> canCloseRequest(req, role))
                .filter(req -> !req.getRequesterId().equals(Integer.valueOf(userId))) // exclude own requests
                .map(AssignRequestDTO::new)
                .collectList()
                .map(RequestResponse::new);
    }

    public Mono<RequestResponse> viewReturnNotice(Integer id, String userId, String role) {
        return requestRepository.findById(id)
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.NOT_FOUND)))
                .filter(req -> req.getReturnSubmittedAt() != null
                        && "DELIVERED".equalsIgnoreCase(req.getStatus()))  // is closable
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.INVALID_OPERATION)))
                .filter(req -> canCloseRequest(req, role))      // role check
                .filter(req -> !req.getRequesterId().equals(Integer.valueOf(userId))) // exclude own
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.UNAUTHORIZED)))
                .map(AssignRequestDTO::new)
                .map(req -> new RequestResponse(List.of(req)));
    }

    public Mono<ApiResponse> confirmReturnNotice(Integer id, String userId, String role, String authHeader) {
        return requestRepository.findById(id)
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.NOT_FOUND)))
                .filter(req -> "DELIVERED".equalsIgnoreCase(req.getStatus())
                        && req.getReturnSubmittedAt() != null)
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.INVALID_OPERATION)))
                .filter(req -> canCloseRequest(req, role)
                        && !req.getRequesterId().equals(Integer.valueOf(userId))) // exclude own requests
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.UNAUTHORIZED)))
                .flatMap(request -> updateDeviceAssignment(
                        request,
                        new UpdateAssignmentDTO("AVAILABLE", null),
                        authHeader
                )
                .map(req -> {
                    req.setStatus("CLOSED");
                    req.setClosedBy(Integer.valueOf(userId));
                    return req;
                })
                .flatMap(requestRepository::save)
                .map(saved -> new ApiResponse(saved.getId())));
    }

    private boolean isProcessedBy(Request req, Integer userId) {
        return userId.equals(req.getProcessedByManager())
                || userId.equals(req.getProcessedByIt())
                || userId.equals(req.getClosedBy());
    }

    public Mono<RequestResponse> viewMyProcessedRequests(String userId) {
        return requestRepository.findByProcessedByManagerIsNotNull()
                .filter(req -> isProcessedBy(req, Integer.valueOf(userId)))
                .map(AssignRequestDTO::new)
                .collectList()
                .map(RequestResponse::new);
    }

    private Mono<Void> validateDevice(CreateRegistryDTO dto, String authHeader) {
        return webClient.post()
                .uri("http://localhost:8081/device/registration/validate")
                .header("Authorization", authHeader)
                .header("X-Service-Source", "request-service")
                .bodyValue(dto)
                .retrieve()
                .onStatus(status -> status.value() == 400,
                        response ->
                                Mono.error(new AppException(ErrorCode.MISSING_FIELDS))
                )
                .onStatus(status -> status.value() == 404,
                        response ->
                                Mono.error(new AppException(ErrorCode.TYPE_NOT_FOUND))
                )
                .onStatus(status -> status.value() == 409,
                        response ->
                                Mono.error(new AppException(ErrorCode.DUPLICATE_SERIAL))
                )
                .toBodilessEntity() // don't retrieve response
                .then();
    }

    public Mono<ApiResponse> createRegistry(CreateRegistryDTO dto, String userId, String authHeader) {
        return validateDevice(dto, authHeader)
                .then(requestRepository.save(
                        new Request("REGISTER", Integer.valueOf(userId), dto.getReason())))
                .flatMap(savedRequest ->
                        registryRepository.save(new Registry(savedRequest.getId(), dto))
                                .thenReturn(new ApiResponse(savedRequest.getId())));
    }

    public Mono<MyRegistryResponse> viewAllMyRegistries(String userId) {
        return registryRepository.findAllRegistryByRequesterId(Integer.valueOf(userId))
                .map(ViewMyRegistryDTO::new)
                .collectList()
                .map(MyRegistryResponse::new);
    }

    public Mono<MyRegistryResponse> viewMyRegistry(Integer id, String userId) {
        return requestRepository.findById(id)
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.NOT_FOUND)))
                .filter(request -> request.getRequesterId().equals(Integer.valueOf(userId)))
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.UNAUTHORIZED)))
                .flatMap(request -> registryRepository.findByRequestId(id))
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.REGISTRY_NOT_FOUND)))
                .map(ViewMyRegistryDTO::new)
                .map(registry -> new MyRegistryResponse(List.of(registry)));
    }

}
