package com.example.request.dto;

import com.example.request.model.Request;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PendingRequestDTO {
    private Integer id;
    private UUID deviceUuid;
    private Integer requesterId;
    private String reason;
    private String status;
    private Instant createdAt;
    private Integer processedByManager;
    private Instant managerProcessedAt;
    private String managerComment;

    public PendingRequestDTO(Request request)
    {
        this.id = request.getId();
        this.deviceUuid = request.getDeviceUuid();
        this.requesterId = request.getRequesterId();
        this.reason = request.getReason();
        this.status = request.getStatus();
        this.createdAt = request.getCreatedAt();
        this.processedByManager = request.getProcessedByManager();
        this.managerProcessedAt = request.getManagerProcessedAt();
        this.managerComment = request.getManagerComment();
    }
}
