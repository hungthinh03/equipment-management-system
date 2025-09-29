package com.example.request.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdateAssignmentDTO {
    private String status;
    private Integer assignedTo;
}
