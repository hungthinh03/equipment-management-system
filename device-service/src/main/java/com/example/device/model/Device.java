package com.example.device.model;

import com.example.device.dto.AddDeviceDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("devices")
public class Device {
    @Id
    @Column("id")
    private Integer id;

    @Column("uuid")
    private UUID uuid;

    @Column("name")
    private String name;

    @Column("type_id")
    private Integer typeId;

    @Column("status")
    private String status;

    @Column("assigned_to")
    private Integer assignedTo;

    @Column("ownership_type")
    private String ownershipType; // COMPANY, BYOD

    @Column("owned_by")
    private Integer ownedBy; // userId if BYOD, else null

    @Column("purchase_price")
    private Double purchasePrice;

    @Column("purchase_date")
    private Instant purchaseDate;

    @Column("serial_number")
    private String serialNumber;

    @Column("manufacturer")
    private String manufacturer;

    @Column("created_by")
    private Integer createdBy;

    @Column("created_at")
    private Instant createdAt;

    @Column("updated_at")
    private Instant updatedAt;

    @Column("decommission_at")
    private Instant decommissionAt;


    public Device(AddDeviceDTO dto, Integer typeId) {
        this.name = dto.getName();
        this.typeId = typeId;
    }

}
