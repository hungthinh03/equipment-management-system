package com.example.device.controller;

import com.example.device.common.enums.ErrorCode;
import com.example.device.common.exception.AppException;
import com.example.device.dto.*;
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
    public Mono<ApiResponse> addDevice(@RequestBody AddDeviceDTO request,
                                       @RequestHeader("X-User-Role") String role) {
        return validateRole(role)
                .flatMap(r -> deviceService.addDevice(request, r));
    }

    @PutMapping("/{id}")
    public Mono<ApiResponse> updateDevice(@RequestBody AddDeviceDTO request,
                                          @RequestHeader("X-User-Role") String role,
                                          @PathVariable Integer id) {
        return validateRole(role)
                .flatMap(r -> deviceService.updateDevice(request, r, id));
    }

    @GetMapping("/{id}")
    public Mono<DeviceResponse> viewDevice(@RequestHeader("X-User-Role") String role,
                                           @PathVariable Integer id) {
        return validateRole(role)
                .flatMap(r -> deviceService.viewDevice(r, id));
    }

    @GetMapping
    public Mono<DeviceResponse> viewAllDevices(@RequestHeader("X-User-Role") String role) {
        return validateRole(role)
                .flatMap(r -> deviceService.viewAllDevices(r));
    }

    @GetMapping("/search")
    public Mono<SearchResponse> searchDevices(@RequestParam(required = false) String name,
                                              @RequestParam(required = false) String type) {
        return deviceService.searchDevices(name, type);
    }

    @GetMapping("/by-uuid/{uuid}")
    public Mono<SearchResponse> viewDeviceByUuid(@PathVariable String uuid) {
        return deviceService.viewDeviceByUuid(uuid);
    }

    @DeleteMapping("/{id}")
    public Mono<ApiResponse> decommissionDevice(@RequestHeader("X-User-Role") String role,
                                                @PathVariable Integer id) {
        return validateRole(role)
                .flatMap(r -> deviceService.decommissionDevice(r, id));
    }

    @PutMapping("/by-uuid/{uuid}")
    public Mono<ApiResponse> updateDeviceAssignment(@RequestBody UpdateStatusDTO dto,
                                                @RequestHeader("X-User-Role") String role,
                                                @RequestHeader(value = "X-Service-Source", required = false) String source,
                                                @PathVariable String uuid) {
        return Mono.justOrEmpty(source)
                .filter("request-service"::equalsIgnoreCase)
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.UNAUTHORIZED)))
                .then(validateRole(role))
                .flatMap(r -> deviceService.updateDeviceAssignment(dto, r, uuid));
    }


}
