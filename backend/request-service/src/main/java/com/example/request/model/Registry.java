package com.example.request.model;

import com.example.request.dto.CreateRegistryDTO;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@NoArgsConstructor
@Table("registries")
public class Registry {
    @Id
    @Column("id")
    private Integer id;

    @Column("request_id")
    private Integer requestId;  // foreign key to requests

    @Column("name")
    private String name;

    @Column("type")
    private String type;

    @Column("serial_number")
    private String serialNumber;

    @Column("manufacturer")
    private String manufacturer;

    public Registry(Integer requestId, CreateRegistryDTO dto) {
        this.requestId = requestId;
        this.name = dto.getName();
        this.type = dto.getType();
        this.serialNumber = dto.getSerialNumber();
        this.manufacturer = dto.getManufacturer();
    }

}
