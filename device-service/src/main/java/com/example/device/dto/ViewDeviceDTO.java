package com.example.device.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ViewDeviceDTO {
    private Integer id;
    private String uuid;
    private String name;
    private String type;       // get type name from id
    private String serialNumber;
    private String manufacturer;
    private String ownershipType; // COMPANY, BYOD
    private Integer ownedBy; // userId if BYOD, else null
    private String status;
    private Integer assignedTo;
    private Instant createdAt;
    private Integer createdBy;
    private Instant updatedAt;
    private Integer updatedBy;
    private Instant decommissionAt;
}
