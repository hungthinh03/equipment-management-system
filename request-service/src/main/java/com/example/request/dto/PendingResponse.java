package com.example.request.dto;

import lombok.Data;

import java.util.List;

@Data
public class PendingResponse {
    private String status;
    private List<PendingRequestDTO> requests;

    public PendingResponse(List<PendingRequestDTO> requests) {
        this.status = "success";
        this.requests = requests;
    }
}
