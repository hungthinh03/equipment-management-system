package com.example.report.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class DeviceDTO {
    private Integer id;
    private String uuid;
    private String name;
    private String type;
    private String serialNumber;
    private String manufacturer;
    private String ownershipType;
    private Integer ownedBy;
    private String status;
    private Integer assignedTo;
    private Instant createdAt;
    private Integer createdBy;
    private Instant updatedAt;
    private Integer updatedBy;
    private Instant retiredAt;
}
