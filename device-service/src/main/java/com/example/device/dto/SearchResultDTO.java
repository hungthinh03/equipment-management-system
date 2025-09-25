package com.example.device.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchResultDTO {
    private String uuid;
    private String name;
    private String type;
    private String category;
    private String status;
    private Instant updatedAt;

    public SearchResultDTO(String uuid, String category) {
        this.uuid = uuid;
        this.category = category;
    }
}
