package com.example.device.model;

import com.example.device.dto.AddDeviceDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("devices")
public class Device {
    @Id
    @Column("id")
    private Integer id;

    @Column("uuid")
    private String uuid;

    @Column("name")
    private String name;

    @Column("type_id")
    private Integer typeId;

    @Column("status")
    private String status;

    @Column("assigned_to")
    private Integer assignedTo;

    @Column("created_at")
    private Instant createdAt;

    @Column("updated_at")
    private Instant updatedAt;


    public Device(AddDeviceDTO dto, Integer typeId) {
        this.name = dto.getName();
        this.typeId = typeId;
    }

}
