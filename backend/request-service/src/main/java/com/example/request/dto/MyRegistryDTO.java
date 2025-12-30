package com.example.request.dto;

import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
public class MyRegistryDTO {
    private Integer requestId;
    private UUID deviceUuid;
    private String name;      // register device
    private String type;
    private String serialNumber;
    private String manufacturer;
    private String reason;
    private String status;
    private Instant createdAt;
    private Instant UpdatedAt;
    private Instant releaseSubmittedAt;
}