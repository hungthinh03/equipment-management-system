package com.example.device.controller;

import com.example.device.dto.ApiResponseDTO;
import com.example.device.dto.DeviceDTO;
import com.example.device.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/device")
public class DeviceController {
    @Autowired
    private DeviceService deviceService;

    //
    @PostMapping("/add")
    public Mono<ApiResponseDTO> addDevice(@RequestBody DeviceDTO request) {
        return deviceService.addDevice(request);
    }
}
