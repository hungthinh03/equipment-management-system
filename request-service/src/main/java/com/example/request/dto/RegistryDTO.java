package com.example.request.dto;

import lombok.Data;

import java.time.Instant;

@Data
public class RegistryDTO {
    private Integer requestId;
    private String name;                    // register device
    private String type;
    private String serialNumber;
    private String manufacturer;
    private String reason;
    private String status;
    private Instant createdAt;
    private Instant UpdatedAt;
}