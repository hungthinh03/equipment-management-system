package com.example.device.service;

import com.example.device.common.enums.ErrorCode;
import com.example.device.common.exception.AppException;
import com.example.device.dto.ApiResponseDTO;
import com.example.device.dto.DeviceDTO;
import com.example.device.model.Device;
import com.example.device.repository.DeviceRepository;
import com.example.device.repository.DeviceTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.stream.Stream;


@Service
public class DeviceServiceImpl implements DeviceService {
    @Autowired
    private DeviceRepository deviceRepo;

    @Autowired
    private DeviceTypeRepository deviceTypeRepo;

    private Mono<DeviceDTO> validateDevice(DeviceDTO dto) {
        return Mono.justOrEmpty(dto) // stream of elements ["a", "b", "c"]
                .filter(d -> Stream.of(d.getName(), d.getType()).allMatch(Objects::nonNull))
                .filter(d -> Stream.of(d.getName(), d.getType()).noneMatch(String::isBlank))
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.INVALID_INPUT)));
                /*
                .map(d -> {
                        d.setCategory(DeviceCategory.valueOf(d.getCategory().toUpperCase()).toString());
                        return d; // Return INVALID_CATEGORY when don't match DeviceCategory enum
                })
                .onErrorMap(IllegalArgumentException.class,
                        e -> new AppException(ErrorCode.INVALID_CATEGORY));

                 */
    }

    private boolean isRoleAllowed(String role, String category) {
        return ("ADMIN".equals(role) && "GENERAL".equals(category))
                || ("IT".equals(role) && "NETWORK".equals(category));
    }

    /*
    private Mono<DeviceDTO> validateRole(DeviceDTO dto, String role) {
        return Mono.just(dto)
                .filter(d -> isRoleAllowed(role,))
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.UNAUTHORIZED)));
    }

     */


    public Mono<ApiResponseDTO> addDevice(DeviceDTO dto, String role) {
        return validateDevice(dto)
                .flatMap(d -> deviceTypeRepo.findByName(d.getType())
                        .switchIfEmpty(Mono.error(new AppException(ErrorCode.INVALID_INPUT)))
                )
                .flatMap(deviceType -> Mono.just(deviceType)
                        .filter(dt -> isRoleAllowed(role, dt.getCategory()))
                        .switchIfEmpty(Mono.error(new AppException(ErrorCode.UNAUTHORIZED)))
                )
                .flatMap(deviceType -> deviceRepo.save(new Device(dto, deviceType.getId())))
                .map(savedDevice -> new ApiResponseDTO(savedDevice.getId()))
                .onErrorResume(AppException.class,
                        e -> Mono.just(new ApiResponseDTO(e.getErrorCode()))
                );
    }





}
