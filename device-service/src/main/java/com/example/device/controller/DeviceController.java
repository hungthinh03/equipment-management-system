package com.example.device.controller;

import com.example.device.annotation.RequireRole;
import com.example.device.common.enums.ErrorCode;
import com.example.device.common.exception.AppException;
import com.example.device.dto.*;
import com.example.device.response.ApiResponse;
import com.example.device.response.DeviceResponse;
import com.example.device.response.SearchResponse;
import com.example.device.response.TypeResponse;
import com.example.device.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/device")
public class DeviceController {
    @Autowired
    private DeviceService deviceService;
    

    @PostMapping
    @RequireRole({"ADMIN", "IT"})
    public Mono<ApiResponse> addDevice(@RequestBody AddDeviceDTO request,
                                       @RequestHeader("X-User-Id") String userId,
                                       @RequestHeader("X-User-Role") String role) {
        return deviceService.addDevice(request, userId, role);
    }

    @PutMapping("/{id}")
    @RequireRole({"ADMIN", "IT"})
    public Mono<ApiResponse> updateDevice(@RequestBody AddDeviceDTO request,
                                          @RequestHeader("X-User-Id") String userId,
                                          @RequestHeader("X-User-Role") String role,
                                          @PathVariable Integer id) {
        return deviceService.updateDevice(request, userId, role, id);
    }

    @GetMapping("/{id}")
    @RequireRole({"ADMIN", "IT"})
    public Mono<DeviceResponse> viewDevice(@RequestHeader("X-User-Role") String role,
                                           @PathVariable Integer id) {
        return deviceService.viewDevice(role, id);
    }

    @GetMapping
    @RequireRole({"ADMIN", "IT"})
    public Mono<DeviceResponse> viewAllDevices(@RequestHeader("X-User-Role") String role) {
        return deviceService.viewAllDevices(role);
    }

    @GetMapping("/type")
    @RequireRole({"ADMIN", "IT"})
    public Mono<TypeResponse> viewAllDeviceTypes(@RequestHeader("X-User-Role") String role) {
        return deviceService.viewAllDeviceTypes(role);
    }

    @GetMapping("/search")
    public Mono<SearchResponse> searchDevices(@RequestParam(required = false) String name,
                                              @RequestParam(required = false) String type,
                                              @RequestParam(defaultValue = "0") int page) {
        return deviceService.searchDevices(name, type, page-1);
    }

    @GetMapping("/by-uuid/{uuid}")
    public Mono<SearchResponse> viewDeviceByUuid(@PathVariable String uuid) {
        return deviceService.viewDeviceByUuid(uuid);
    }

    @PutMapping("/maintenance/{id}")
    @RequireRole({"ADMIN", "IT"})
    public Mono<ApiResponse> updateDeviceMaintenance(@RequestBody MaintenanceDTO request,
                                                     @RequestHeader("X-User-Id") String userId,
                                                     @RequestHeader("X-User-Role") String role,
                                                     @PathVariable Integer id) {
        return deviceService.updateDeviceMaintenance(request.getMaintenance(), userId, role, id);
    }

    @DeleteMapping("/{id}")
    @RequireRole({"ADMIN", "IT"})
    public Mono<ApiResponse> decommissionDevice(@RequestHeader("X-User-Id") String userId,
                                                @RequestHeader("X-User-Role") String role,
                                                @PathVariable Integer id) {
        return deviceService.decommissionDevice(userId, role, id);
    }

    @PutMapping("/by-uuid/{uuid}")
    @RequireRole({"ADMIN", "IT"}) // needs @RequestHeader("X-User-Role")
    public Mono<ApiResponse> updateDeviceAssignment(@RequestBody UpdateStatusDTO dto,
                                                    @RequestHeader("X-User-Id") String userId,
                                                    @RequestHeader("X-User-Role") String role,
                                                    @RequestHeader(value = "X-Service-Source", required = false) String source,
                                                    @PathVariable String uuid) {
        return Mono.justOrEmpty(source)
                .filter("request-service"::equalsIgnoreCase)
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.UNAUTHORIZED)))
                .flatMap(r -> deviceService.updateDeviceAssignment(dto, userId, uuid));
    }

    @PutMapping("registration/validate")
    public Mono<ApiResponse> validateDeviceRegistration(@RequestBody RegistryDTO dto) {
        return deviceService.validateDeviceRegistration(dto);
    }
}
