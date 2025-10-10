package com.example.device.service;


import com.example.device.dto.*;
import com.example.device.response.ApiResponse;
import com.example.device.response.DeviceResponse;
import com.example.device.response.SearchResponse;
import com.example.device.response.TypeResponse;
import reactor.core.publisher.Mono;


public interface DeviceService {
    Mono<ApiResponse> addDevice(AddDeviceDTO dto, String userId, String role);

    Mono<ApiResponse> updateDevice(AddDeviceDTO request, String userId, String role, Integer id);

    Mono<DeviceResponse> viewDevice(String role, Integer id);

    Mono<DeviceResponse> viewAllDevices(String role);

    Mono<TypeResponse> viewAllDeviceTypes(String role);

    Mono<SearchResponse> searchDevices(String name, String type, int page);

    Mono<SearchResponse> viewDeviceByUuid(String uuid);

    Mono<ApiResponse> updateDeviceMaintenance(Boolean maintenance, String userId, String role, Integer id);

    Mono<ApiResponse> decommissionDevice(String userId, String role, Integer id);

    Mono<ApiResponse> updateDeviceAssignment(UpdateStatusDTO dto, String userId, String uuid);

    Mono<ApiResponse> validateDeviceRegistration(RegistryDTO dto);
}
