package com.example.device.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateStatusDTO {
    private String status;
    private Integer AssignedTo;
}
