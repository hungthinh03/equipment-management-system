package com.example.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateRequestDTO {
    private String uuid;         // Public UUID for employee-facing API
    private String reason;
}
