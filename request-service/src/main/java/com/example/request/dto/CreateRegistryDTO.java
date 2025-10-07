package com.example.request.dto;


import lombok.Data;

@Data
public class CreateRegistryDTO {
    private String name;
    private String type;
    private String serialNumber;
    private String manufacturer;
}
