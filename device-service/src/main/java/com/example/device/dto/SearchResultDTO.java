package com.example.device.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchResultDTO {
    private String uuid;
    private String name;
    private String type;
    private String status;
    private Instant updatedAt;
}
