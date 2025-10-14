package com.example.request.dto;

import com.example.request.model.Request;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AssignRequestDTO {
    private Integer id;
    private String requestType;
    private UUID deviceUuid;
    private Integer requesterId;
    private String reason;
    private String status;
    private Instant createdAt;
    private Integer processedByManager;
    private Instant managerProcessedAt;
    private String managerComment;
    private Integer processedByIt;
    private Instant itProcessedAt;
    private String itComment;
    private Integer deliveredBy;
    private Instant deliveredAt;
    private Instant returnSubmittedAt;
    private Integer closedBy;

    public AssignRequestDTO(Request request)
    {
        this.id = request.getId();
        this.requestType = request.getRequestType();
        this.deviceUuid = request.getDeviceUuid();
        this.requesterId = request.getRequesterId();
        this.reason = request.getReason();
        this.status = request.getStatus();
        this.createdAt = request.getCreatedAt();
        this.processedByManager = request.getProcessedByManager();
        this.managerProcessedAt = request.getManagerProcessedAt();
        this.managerComment = request.getManagerComment();
        this.processedByIt = request.getProcessedByIt();
        this.itProcessedAt = request.getItProcessedAt();
        this.itComment = request.getItComment();
        this.deliveredBy = request.getDeliveredBy();
        this.deliveredAt = request.getDeliveredAt();
        this.returnSubmittedAt = request.getReturnSubmittedAt();
        this.closedBy = request.getClosedBy();
    }

    public AssignRequestDTO(RequestDTO request)
    {
        this.id = request.getId();
        this.requestType = request.getRequestType();
        this.deviceUuid = request.getDeviceUuid();
        this.requesterId = request.getRequesterId();
        this.reason = request.getReason();
        this.status = request.getStatus();
        this.createdAt = request.getCreatedAt();
        this.processedByManager = request.getProcessedByManager();
        this.managerProcessedAt = request.getManagerProcessedAt();
        this.managerComment = request.getManagerComment();
        this.processedByIt = request.getProcessedByIt();
        this.itProcessedAt = request.getItProcessedAt();
        this.itComment = request.getItComment();
        this.deliveredBy = request.getDeliveredBy();
        this.deliveredAt = request.getDeliveredAt();
        this.returnSubmittedAt = request.getReturnSubmittedAt();
        this.closedBy = request.getClosedBy();
    }
}
