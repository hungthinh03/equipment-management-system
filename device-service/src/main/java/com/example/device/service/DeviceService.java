package com.example.device.service;


import com.example.device.dto.ApiResponseDTO;
import com.example.device.dto.AddDeviceDTO;
import com.example.device.dto.DeviceResponseDTO;
import com.example.device.dto.SearchResponseDTO;
import reactor.core.publisher.Mono;

public interface DeviceService {
    Mono<ApiResponseDTO> addDevice(AddDeviceDTO dto, String role);

    Mono<ApiResponseDTO> updateDevice(AddDeviceDTO request, String role, Integer id);

    Mono<DeviceResponseDTO> viewDevice(String role, Integer id);

    Mono<DeviceResponseDTO> viewAllDevices(String role);

    Mono<SearchResponseDTO> searchDevices(String name, String type);
}
