package com.example.device.dto;

import com.example.device.common.enums.DeviceCategory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceDTO {
    private String name;
    private String type;
    private DeviceCategory category;
}
