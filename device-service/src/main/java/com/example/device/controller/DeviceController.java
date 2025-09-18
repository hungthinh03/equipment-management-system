package com.example.device.controller;

import com.example.device.common.enums.ErrorCode;
import com.example.device.common.exception.AppException;
import com.example.device.dto.ApiResponseDTO;
import com.example.device.dto.AddDeviceDTO;
import com.example.device.dto.DeviceResponseDTO;
import com.example.device.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/device")
public class DeviceController {
    @Autowired
    private DeviceService deviceService;

    private Mono<String> validateRole(String role) {
        return Mono.just(role)
                .filter(r -> "ADMIN".equals(r) || "IT".equals(r))
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.UNAUTHORIZED)));
    }

    @PostMapping
    public Mono<ApiResponseDTO> addDevice(@RequestBody AddDeviceDTO request,
                                          @RequestHeader("X-User-Role") String role) {
        return validateRole(role)
                .flatMap(r -> deviceService.addDevice(request, r));
    }

    @PutMapping("/{id}")
    public Mono<ApiResponseDTO> updateDevice(@RequestBody AddDeviceDTO request,
                                             @RequestHeader("X-User-Role") String role,
                                             @PathVariable Integer id) {
        return validateRole(role)
                .flatMap(r -> deviceService.updateDevice(request, r, id));
    }

    @GetMapping("/{id}")
    public Mono<DeviceResponseDTO> viewDevice(@RequestHeader("X-User-Role") String role,
                                              @PathVariable Integer id) {
        return validateRole(role)
                .flatMap(r -> deviceService.viewDevice(r, id));
    }

    @GetMapping
    public Mono<DeviceResponseDTO> viewAllDevices(@RequestHeader("X-User-Role") String role) {
        return validateRole(role)
                .flatMap(r -> deviceService.viewAllDevices(r));
    }

}
