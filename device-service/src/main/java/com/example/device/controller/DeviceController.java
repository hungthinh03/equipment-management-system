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
                                       @RequestHeader("X-User-Id") String userId,
                                       @RequestHeader("X-User-Role") String role) {
        return validateRole(role)
                .flatMap(r -> deviceService.addDevice(request, userId, r));
    }

    @PutMapping("/{id}")
    public Mono<ApiResponse> updateDevice(@RequestBody AddDeviceDTO request,
                                          @RequestHeader("X-User-Id") String userId,
                                          @RequestHeader("X-User-Role") String role,
                                          @PathVariable Integer id) {
        return validateRole(role)
                .flatMap(r -> deviceService.updateDevice(request, userId, r, id));
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

    @GetMapping("/type")
    public Mono<TypeResponse> viewAllDeviceTypes(@RequestHeader("X-User-Role") String role) {
        return validateRole(role)
                .flatMap(r -> deviceService.viewAllDeviceTypes(r));
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

    @PutMapping("/maintenance/{id}")
    public Mono<ApiResponse> updateDeviceMaintenance(@RequestBody MaintenanceDTO request,
                                                     @RequestHeader("X-User-Id") String userId,
                                                     @RequestHeader("X-User-Role") String role,
                                                     @PathVariable Integer id) {
        return validateRole(role)
                .flatMap(r -> deviceService.updateDeviceMaintenance(request.getMaintenance(), userId, r, id));
    }

    @DeleteMapping("/{id}")
    public Mono<ApiResponse> decommissionDevice(@RequestHeader("X-User-Id") String userId,
                                                @RequestHeader("X-User-Role") String role,
                                                @PathVariable Integer id) {
        return validateRole(role)
                .flatMap(r -> deviceService.decommissionDevice(userId, r, id));
    }

    @PutMapping("/by-uuid/{uuid}")
    public Mono<ApiResponse> updateDeviceAssignment(@RequestBody UpdateStatusDTO dto,
                                                    @RequestHeader("X-User-Id") String userId,
                                                    @RequestHeader("X-User-Role") String role,
                                                    @RequestHeader(value = "X-Service-Source", required = false) String source,
                                                    @PathVariable String uuid) {
        return Mono.justOrEmpty(source)
                .filter("request-service"::equalsIgnoreCase)
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.UNAUTHORIZED)))
                .then(validateRole(role))
                .flatMap(r -> deviceService.updateDeviceAssignment(dto, userId, uuid));
    }


}
