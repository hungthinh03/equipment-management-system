package com.example.device.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchResultDTO {
    private String uuid;
    private String name;
    private String type;
    private String status;
}
