package com.example.device.dto;

import lombok.Data;

import java.util.List;

@Data
public class TypeResponse {
    private String status;
    private List<String> deviceType;

    public TypeResponse(List<String> type)
    {
        this.status = "success";
        this.deviceType = type;
    }
}
