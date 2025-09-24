package com.example.request.dto;

import lombok.Data;

@Data
public class CreateRequestDTO {
    private String uuid;         // Public UUID for employee-facing API
    private String reason;
}
