package com.example.request.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegisterDeviceDTO {
    private String name;
    private String type;
    private String serialNumber;
    private String manufacturer;
}
