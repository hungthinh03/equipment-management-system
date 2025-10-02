package com.example.request.dto;

import com.example.request.model.Request;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ViewMyRequestDTO {
    private Integer id;
    private UUID deviceUuid;         // Public UUID for employee-facing API
    private String reason;
    private String status;           // PENDING, APPROVED, REJECTED, CLOSED
    private Instant createdAt;
    private Instant UpdatedAt;
    private Instant requestedToCloseAt;
    private Instant closedAt;

    public ViewMyRequestDTO(Request request) {
        this.id = request.getId();
        this.deviceUuid = request.getDeviceUuid();
        this.reason = request.getReason();
        this.status = request.getStatus();
        this.createdAt = request.getCreatedAt();
        this.UpdatedAt = request.getUpdatedAt();
        this.requestedToCloseAt = request.getRequestedToCloseAt();
        this.closedAt = request.getClosedAt();
    }
}
