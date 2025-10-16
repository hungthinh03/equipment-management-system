package com.example.request.dto;


import com.example.request.model.Registry;
import lombok.Data;

@Data
public class CreateRegistryDTO {
    private String name;
    private String type;
    private String serialNumber;
    private String manufacturer;
    private String reason;

    public CreateRegistryDTO(Registry registry) {
        this.name = registry.getName();
        this.type = registry.getType();
        this.serialNumber = registry.getSerialNumber();
        this.manufacturer = registry.getManufacturer();
    }
}
