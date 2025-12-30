package com.example.report.response;

import com.example.report.dto.DeviceDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DeviceResponse {
    private String status;
    private List<DeviceDTO> deviceList;

    public DeviceResponse(List<DeviceDTO> deviceList) {
        this.status = "success";
        this.deviceList = deviceList;
    }

}
