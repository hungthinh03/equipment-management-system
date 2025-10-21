package com.example.device.service;


import com.example.device.dto.*;
import com.example.device.response.*;
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

    Mono<ApiResponse> validateDeviceRegistration(RegisterDeviceDTO dto);

    Mono<ApiResponse> registerDevice(RegisterDeviceDTO dto, String userId);

    Mono<MyDeviceResponse> viewAllMyDevices(String userId);

    Mono<MyDeviceResponse> viewMyDevice(String userId, String uuid);

    Mono<ApiResponse> unenrollDevice(String userId, String role, String uuid);

    Mono<DeviceResponse> getAllDevicesReport();

    Mono<DeviceResponse> getAllActiveDevicesReport();
}
