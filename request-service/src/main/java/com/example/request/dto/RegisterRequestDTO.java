package com.example.request.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "id", "requestType", "deviceUuid", "device" })
public class RegisterRequestDTO extends AssignRequestDTO {
    private RegisterDeviceDTO device;

    public RegisterRequestDTO(RequestDTO dto) {
        super(dto);
        this.device = new RegisterDeviceDTO(
                dto.getName(),
                dto.getType(),
                dto.getSerialNumber(),
                dto.getManufacturer()
        );

    }
}
