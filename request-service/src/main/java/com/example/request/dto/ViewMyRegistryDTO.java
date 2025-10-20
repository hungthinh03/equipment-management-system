package com.example.request.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ViewMyRegistryDTO {
    private Integer id;
    private UUID deviceUuid;
    private RegisterDeviceDTO device;
    private String reason;
    private String status;                  // PENDING, APPROVED, REJECTED, CLOSED
    private Instant createdAt;
    private Instant UpdatedAt;
    private Instant returnSubmittedAt;

    public ViewMyRegistryDTO(MyRegistryDTO dto) {
        this.id = dto.getRequestId();
        this.deviceUuid = dto.getDeviceUuid();
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
        this.returnSubmittedAt = dto.getReturnSubmittedAt();
    }
}
