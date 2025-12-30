package com.example.device.response;

import com.example.device.dto.MyDeviceDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MyDeviceResponse {
    private String status;
    private MyDeviceDTO device;
    private List<MyDeviceDTO> deviceList;

    public MyDeviceResponse(MyDeviceDTO device) {
        this.status = "success";
        this.device = device;
    }

    public MyDeviceResponse(List<MyDeviceDTO> deviceList) {
        this.status = "success";
        this.deviceList = deviceList;
    }
}
