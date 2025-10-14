package com.example.request.dto;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class RequestDTO {
    private Integer id;
    private Integer requestId;
    private String requestType;
    private UUID deviceUuid;
    private String name;
    private String type;
    private String serialNumber;
    private String manufacturer;
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

}
