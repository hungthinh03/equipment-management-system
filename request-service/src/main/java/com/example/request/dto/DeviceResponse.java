package com.example.request.dto;

import lombok.Data;

@Data
public class DeviceResponse {
    private String status;
    private DeviceStatusDTO result;
}
