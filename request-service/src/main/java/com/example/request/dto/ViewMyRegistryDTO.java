package com.example.request.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.Instant;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ViewMyRegistryDTO {
    private Integer requestId;
    private RegisterDeviceDTO device;
    private String reason;
    private String status;                  // PENDING, APPROVED, REJECTED, CLOSED
    private Instant createdAt;
    private Instant UpdatedAt;

    public ViewMyRegistryDTO(MyRegistryDTO dto) {
        this.requestId = dto.getRequestId();
        this.device = new RegisterDeviceDTO(
                dto.getName(),
                dto.getType(),
                dto.getSerialNumber(),
                dto.getManufacturer()
        );
        this.reason = dto.getReason();
        this.status = dto.getStatus();
        this.createdAt = dto.getCreatedAt();
        this.UpdatedAt = dto.getUpdatedAt();
    }
}
