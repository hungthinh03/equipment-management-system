package com.example.device.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("devices")
public class Device {
    @Id
    @Column("id")
    private Integer id;

    @Column("name")
    private String name;

    @Column("type")
    private String type;

    @Column("status")
    private String status;

    @Column("assigned_to")
    private Integer assignedTo;

    @Column("category")
    private String category;
}
