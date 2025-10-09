package com.example.request.response;

import com.example.request.dto.DeviceStatusDTO;
import lombok.Data;

@Data
public class DeviceResponse {
    private String status;
    private DeviceStatusDTO result;
}
