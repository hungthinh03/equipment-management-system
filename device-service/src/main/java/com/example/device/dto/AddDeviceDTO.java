package com.example.device.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddDeviceDTO {
    private String name;
    private String type;
    private String serialNumber;
    private String manufacturer;
    private BigDecimal purchasePrice;
    private String purchaseDate;
}
