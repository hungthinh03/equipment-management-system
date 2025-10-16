package com.example.device.dto;

import lombok.Data;

@Data
public class RegisterDeviceDTO {
    private String name;
    private String type;
    private String serialNumber;
    private String manufacturer;
    private Integer requesterId;
}
