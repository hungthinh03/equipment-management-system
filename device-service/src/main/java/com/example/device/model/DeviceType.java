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
@Table("device_types")
public class DeviceType {
    @Id
    @Column("id")
    private Integer id;

    @Column("name")
    private String name;

    @Column("category")
    private Integer category;

}
