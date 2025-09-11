package com.example.device.model;

import com.example.device.dto.DeviceDTO;
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

    public Device(DeviceDTO dto)
    {
        this.name = dto.getName();
        this.type = dto.getType();
        this.category = dto.getCategory();
    }
}
