package com.example.request.service;

import com.example.request.common.enums.ErrorCode;
import com.example.request.common.exception.AppException;
import com.example.request.dto.*;
import com.example.request.model.Registry;
import com.example.request.model.Request;
import com.example.request.repository.RegistryRepository;
import com.example.request.repository.RequestRepository;
import com.example.request.response.*;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.ArrayList;
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
                ? requestRepository.findAllPendingRequestsForIT()
                : requestRepository.findAllPendingRequestsForManager()) //Requests admins hasn't approved
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
        return requestRepository.findRequestById(id)
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

    private Mono<Void> updateDeviceAssignment(Request request, UpdateAssignmentDTO dto, String authHeader) {
        return webClient.put()
                .uri("http://localhost:8081/device/by-uuid/{uuid}", request.getDeviceUuid())
                .header("Authorization", authHeader)
                .header("X-Service-Source", "request-service")
                .bodyValue(dto)
                .retrieve()
                .toBodilessEntity()
                .then();
    }

    private Mono<Request> approveAssignment(Request request, String role, String userId,
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
                                        authHeader)
                                        .thenReturn(request);
                            }
                            applyInfoAdmin(request, userId, comment, Instant.now());
                            return Mono.just(request); // else stays PENDING for IT approval
                        });
            } // IT
            applyInfoIt(request, userId, comment, Instant.now());
            request.setStatus("APPROVED");
            return updateDeviceAssignment(
                    request,
                    new UpdateAssignmentDTO("RESERVED", request.getRequesterId()),
                    authHeader)
                    .thenReturn(request);
        });
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

    private Mono<List<String>> getAllDeviceTypesManagedByRole(String authHeader) {
        return webClient.get()
                .uri("http://localhost:8081/device/type")
                .header("Authorization", authHeader)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(json -> {
                    List<String> typeList = new ArrayList<>();
                    json.get("deviceType").forEach(node -> typeList.add(node.asText()));
                    return typeList;
                });
    }

    private Mono<UUID> addRegisterDevice(CreateRegistryDTO dto, String authHeader) {
        return webClient.post()
                .uri("http://localhost:8081/device/registration")
                .header("Authorization", authHeader)
                .header("X-Service-Source", "request-service")
                .bodyValue(dto)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(json -> UUID.fromString(json.get("uuid").asText()));
    }

    private Mono<Request> approveRegistry(Request request, String role, String userId,
                                          String comment, Registry device, String authHeader) {
        return validateDevice(new CreateRegistryDTO(device), authHeader)
                .then(Mono.defer(() -> {
                    if ("ADMIN".equalsIgnoreCase(role)) {
                            return getAllDeviceTypesManagedByRole(authHeader)
                                    .flatMap(typeList -> {
                                        applyInfoAdmin(request, userId, comment, Instant.now());
                                        if (typeList.contains(device.getType())) {
                                            request.setStatus("APPROVED"); // IT approval not needed
                                            return addRegisterDevice(
                                                    new CreateRegistryDTO(device, request.getRequesterId()),
                                                    authHeader
                                            ).map(uuid -> {
                                                request.setDeviceUuid(uuid);
                                                return request;
                                            });
                                        }
                                        return Mono.just(request); // stays PENDING
                                    });
                        } // IT
                        applyInfoIt(request, userId, comment, Instant.now());
                        request.setStatus("APPROVED");
                        return addRegisterDevice(
                                new CreateRegistryDTO(device, request.getRequesterId()),
                                authHeader
                        ).map(uuid -> {
                            request.setDeviceUuid(uuid);
                            return request;
                        });
                }));
    }

    private Mono<Request> approveRequest(Request request, String role, String userId, String comment, String authHeader) {
        return "ASSIGN".equalsIgnoreCase(request.getRequestType())
                ? approveAssignment(request, role, userId, comment, authHeader)
                : registryRepository.getDeviceTypeByRequestId(request.getId())
                .flatMap(registry ->
                        approveRegistry(request, role, userId, comment, registry, authHeader)
                );
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
                        : denyRequest(request, role, userId, dto.getComment())
                )
                .flatMap(request -> requestRepository.save(request))
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
                            authHeader)
                            .thenReturn(request);
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
                .filter(req -> req.getReleaseSubmittedAt() == null)
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.ALREADY_REQUESTED_CLOSE)))
                .flatMap(req -> {
                    req.setReleaseSubmittedAt(Instant.now());
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
        return requestRepository.findByReleaseSubmittedAtIsNotNullAndStatus("DELIVERED")
                .filter(req -> canCloseRequest(req, role))
                .filter(req -> !req.getRequesterId().equals(Integer.valueOf(userId))) // exclude own requests
                .map(AssignRequestDTO::new)
                .collectList()
                .map(RequestResponse::new);
    }

    public Mono<RequestResponse> viewReturnNotice(Integer id, String userId, String role) {
        return requestRepository.findById(id)
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.NOT_FOUND)))
                .filter(req -> req.getReleaseSubmittedAt() != null
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
                        && req.getReleaseSubmittedAt() != null)
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.INVALID_OPERATION)))
                .filter(req -> canCloseRequest(req, role)
                        && !req.getRequesterId().equals(Integer.valueOf(userId))) // exclude own requests
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.UNAUTHORIZED)))
                .flatMap(request -> {
                    request.setStatus("CLOSED");
                    request.setClosedBy(Integer.valueOf(userId));
                    return updateDeviceAssignment(
                            request,
                            new UpdateAssignmentDTO("AVAILABLE", null),
                            authHeader
                    ).thenReturn(request);
                })
                .flatMap(requestRepository::save)
                .map(saved -> new ApiResponse(saved.getId()));
    }

    private boolean isProcessedBy(Request req, Integer userId) {
        return userId.equals(req.getProcessedByManager())
                || userId.equals(req.getProcessedByIt())
                || userId.equals(req.getClosedBy());
    }

    public Mono<RequestResponse> viewMyProcessedRequests(String userId) {
        return requestRepository.findRequestByProcessedByManagerIsNotNull()
                .filter(dto -> isProcessedBy(new Request(dto), Integer.valueOf(userId)))
                .map(dto -> "REGISTER".equalsIgnoreCase(dto.getRequestType())
                        ? new RegisterRequestDTO(dto)
                        : new AssignRequestDTO(dto))
                .collectList()
                .map(RequestResponse::new);
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

    public Mono<ApiResponse> submitUnenrollNotice(Integer id, String userId) {
        return requestRepository.findById(id)
                .filter(req -> req.getRequesterId().equals(Integer.valueOf(userId)))
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.UNAUTHORIZED))) // Can only submit for own requests
                .filter(req ->
                        "REGISTER".equalsIgnoreCase(req.getRequestType()) &&
                        "APPROVED".equalsIgnoreCase(req.getStatus()))
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.INVALID_OPERATION)))
                .filter(req -> req.getReleaseSubmittedAt() == null)
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.ALREADY_REQUESTED_CLOSE)))
                .flatMap(req -> {
                    req.setReleaseSubmittedAt(Instant.now());
                    return requestRepository.save(req);
                })
                .map(req -> new ApiResponse(req.getId()));
    }

    public Mono<RequestResponse> viewAllUnenrollNotices(String userId, String role) {
        return requestRepository.findRequestByReleaseSubmittedAtIsNotNullAndStatus("APPROVED")
                .filter(dto -> canCloseRequest(new Request(dto), role))
                .filter(dto -> !dto.getRequesterId().equals(Integer.valueOf(userId))) // exclude own requests
                .map(RegisterRequestDTO::new)
                .collectList()
                .map(RequestResponse::new);
    }

    public Mono<RequestResponse> viewUnenrollNotice(Integer id, String userId, String role) {
        return requestRepository.findRequestById(id)
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.NOT_FOUND)))
                .filter(dto ->
                        dto.getReleaseSubmittedAt() != null &&
                        "APPROVED".equalsIgnoreCase(dto.getStatus()))
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.INVALID_OPERATION)))
                .filter(dto -> canCloseRequest(new Request(dto), role))
                .filter(dto -> !dto.getRequesterId().equals(Integer.valueOf(userId)))
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.UNAUTHORIZED)))
                .map(RegisterRequestDTO::new)
                .map(req -> new RequestResponse(List.of(req)));
    }

    private Mono<Void> unenrollDevice(Request request, String authHeader) {
        return webClient.delete()
                .uri("http://localhost:8081/device/by-uuid/{uuid}", request.getDeviceUuid())
                .header("Authorization", authHeader)
                .header("X-Service-Source", "request-service")
                .retrieve()
                .toBodilessEntity() // don't retrieve response
                .then();
    }

    public Mono<ApiResponse> confirmUnenrollNotice(Integer id, String userId, String role, String authHeader) {
        return requestRepository.findById(id)
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.NOT_FOUND)))
                .filter(dto ->
                        dto.getReleaseSubmittedAt() != null &&
                                "APPROVED".equalsIgnoreCase(dto.getStatus()))
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.INVALID_OPERATION)))
                .filter(dto -> canCloseRequest(dto, role))
                .filter(dto -> !dto.getRequesterId().equals(Integer.valueOf(userId)))
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.UNAUTHORIZED)))
                .flatMap(request -> {
                    request.setStatus("CLOSED");
                    request.setClosedBy(Integer.valueOf(userId));
                    return unenrollDevice(request, authHeader)
                            .thenReturn(request);
                })
                .flatMap(requestRepository::save)
                .map(saved -> new ApiResponse(saved.getId()));
    }

}
