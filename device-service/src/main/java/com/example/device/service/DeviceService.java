package com.example.device.service;


import com.example.device.dto.ApiResponseDTO;
import com.example.device.dto.DeviceDTO;
import reactor.core.publisher.Mono;

public interface DeviceService {
    Mono<ApiResponseDTO> addDevice(DeviceDTO dto, String role);

}
