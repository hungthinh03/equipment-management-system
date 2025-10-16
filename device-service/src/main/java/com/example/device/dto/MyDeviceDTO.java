package com.example.device.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class MyDeviceDTO {
    private String uuid;
    private String name;
    private String type;
    private String serialNumber;
    private String manufacturer;
    private Instant decommissionAt;
}
