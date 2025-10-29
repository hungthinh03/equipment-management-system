package com.example.device.response;

import com.example.device.dto.ViewDeviceDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceResponse {
    private String status;
    private ViewDeviceDTO device;
    private List<ViewDeviceDTO> deviceList;

    public DeviceResponse(ViewDeviceDTO device) {
        this.status = "success";
        this.device = device;
    }

    public DeviceResponse(List<ViewDeviceDTO> deviceList) {
        this.status = "success";
        this.deviceList = deviceList;
    }

}
