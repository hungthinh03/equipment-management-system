package com.example.request.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class CreateRequestDTO {
    private UUID uuid;         // Public UUID for employee-facing API
    private String reason;
}
