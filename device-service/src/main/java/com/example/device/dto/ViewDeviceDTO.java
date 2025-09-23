package com.example.device.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ViewDeviceDTO {
    private Integer id;
    private String uuid;
    private String name;
    private String type;       // get type name from id
    private String status;
    private Integer assignedTo;
    private Instant createdAt;
    private Instant updatedAt;
}
