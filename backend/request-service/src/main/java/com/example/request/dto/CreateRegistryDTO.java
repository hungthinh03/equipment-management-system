package com.example.request.dto;


import com.example.request.model.Registry;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateRegistryDTO {
    private String name;
    private String type;
    private String serialNumber;
    private String manufacturer;
    private Integer requesterId; // for adding device on approval
    private String reason;

    public CreateRegistryDTO(Registry registry) {
        this.name = registry.getName();
        this.type = registry.getType();
        this.serialNumber = registry.getSerialNumber();
        this.manufacturer = registry.getManufacturer();
    }

    public CreateRegistryDTO(Registry registry, Integer requesterId) {
        this.name = registry.getName();
        this.type = registry.getType();
        this.serialNumber = registry.getSerialNumber();
        this.manufacturer = registry.getManufacturer();
        this.requesterId = requesterId;
    }
}
