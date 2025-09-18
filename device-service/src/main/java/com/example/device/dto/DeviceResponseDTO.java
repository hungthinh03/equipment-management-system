package com.example.device.dto;

import lombok.Data;

import java.util.List;

@Data
public class DeviceResponseDTO {
    private String status;
    private ViewDeviceDTO device;
    private List<ViewDeviceDTO> deviceList;

    public DeviceResponseDTO(ViewDeviceDTO device) {
        this.status = "success";
        this.device = device;
    }

    public DeviceResponseDTO(List<ViewDeviceDTO> deviceList) {
        this.status = "success";
        this.deviceList = deviceList;
    }
}
