package com.example.device.service;


import com.example.device.dto.ApiResponse;
import com.example.device.dto.AddDeviceDTO;
import com.example.device.dto.DeviceResponse;
import com.example.device.dto.SearchResponse;
import reactor.core.publisher.Mono;

public interface DeviceService {
    Mono<ApiResponse> addDevice(AddDeviceDTO dto, String role);

    Mono<ApiResponse> updateDevice(AddDeviceDTO request, String role, Integer id);

    Mono<DeviceResponse> viewDevice(String role, Integer id);

    Mono<DeviceResponse> viewAllDevices(String role);

    Mono<SearchResponse> searchDevices(String name, String type);

    Mono<ApiResponse> decommissionDevice(String role, Integer id);
}
