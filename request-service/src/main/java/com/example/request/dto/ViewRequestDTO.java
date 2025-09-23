package com.example.request.dto;

import com.example.request.model.Request;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ViewRequestDTO {
    private Integer id;
    private UUID deviceUuid;         // Public UUID for employee-facing API
    private String reason;
    private String status;           // PENDING, APPROVED, REJECTED, CLOSED
    private Instant createdAt;
    private Instant UpdatedAt;

    public ViewRequestDTO(Request request) {
        this.id = request.getId();
        this.deviceUuid = request.getDeviceUuid();
        this.reason = request.getReason();
        this.status = request.getStatus();
        this.createdAt = request.getCreatedAt();
        this.UpdatedAt = request.getUpdatedAt();

    }
}
