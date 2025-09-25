package com.example.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeviceStatusDTO {
    private String category;
    private String status;
}
