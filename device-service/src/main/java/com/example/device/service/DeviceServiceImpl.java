package com.example.device.service;

import com.example.device.common.enums.DeviceCategory;
import com.example.device.common.enums.ErrorCode;
import com.example.device.common.exception.AppException;
import com.example.device.dto.ApiResponseDTO;
import com.example.device.dto.DeviceDTO;
import com.example.device.model.Device;
import com.example.device.repository.DeviceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.stream.Stream;


@Service
public class DeviceServiceImpl implements DeviceService {
    @Autowired
    private DeviceRepository deviceRepo;

    private Mono<DeviceDTO> validateDeviceDTO(DeviceDTO dto) {
        return Mono.justOrEmpty(dto) // stream of elements ["a", "b", "c"]
                .filter(d -> Stream.of(d.getName(), d.getType(), d.getCategory()).allMatch(Objects::nonNull))
                .filter(d -> Stream.of(d.getName(), d.getType()).noneMatch(String::isBlank))
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.INVALID_INPUT)));
    }

    private boolean isRoleAllowed(DeviceDTO d, String role) {
        return ("ADMIN".equals(role) && d.getCategory() == DeviceCategory.GENERAL)
                || ("IT".equals(role)    && d.getCategory() == DeviceCategory.NETWORK);
    }

    private Mono<DeviceDTO> validateRole(DeviceDTO dto, String role) {
        return Mono.just(dto)
                .filter(d -> isRoleAllowed(d, role))
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.UNAUTHORIZED)));
    }

    public Mono<ApiResponseDTO> addDevice(DeviceDTO dto, String role) {
        return validateDeviceDTO(dto)
                .flatMap(d -> validateRole(d, role)) // role check
                .flatMap(device -> deviceRepo.save(new Device(device)))
                .map(savedDevice -> new ApiResponseDTO(savedDevice.getId()))
                .onErrorResume(AppException.class, e ->
                        Mono.just(new ApiResponseDTO(e.getErrorCode()))
                );
    }



}
