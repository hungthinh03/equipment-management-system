package com.example.device.service;

import com.example.device.common.enums.ErrorCode;
import com.example.device.common.exception.AppException;
import com.example.device.dto.ApiResponseDTO;
import com.example.device.dto.DeviceDTO;
import com.example.device.model.Device;
import com.example.device.repository.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Service
public class DeviceServiceImpl implements DeviceService {
    @Autowired
    private DeviceRepository deviceRepo;

    private Mono<DeviceDTO> validateDeviceDTO(DeviceDTO dto) {
        return Mono.justOrEmpty(dto)
                .filter(d -> d.getName() != null && d.getType() != null && d.getCategory() != null)
                .filter(d -> !d.getName().isBlank() && !d.getType().isBlank())
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.INVALID_INPUT)));
    }


    public Mono<ApiResponseDTO> addDevice(DeviceDTO dto) {
        return validateDeviceDTO(dto)
                .flatMap(device -> deviceRepo.save(new Device(device)))
                .map(savedDevice -> new ApiResponseDTO(savedDevice.getId()))
                .onErrorResume(AppException.class, e ->
                        Mono.just(new ApiResponseDTO(e.getErrorCode()))
                );
    }



}
