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
    }

    //
    public Mono<ApiResponseDTO> addDevice(DeviceDTO dto, String role) {
        return validateDevice(dto)
                .flatMap(d -> deviceTypeRepo.findByNameAndManagedBy(d.getType(), role)
                        .switchIfEmpty(Mono.error(new AppException(ErrorCode.INVALID_TYPE)))
                )
                .flatMap(deviceType -> deviceRepo.save(new Device(dto, deviceType.getId())))
                .map(savedDevice -> new ApiResponseDTO(savedDevice.getId()))
                .onErrorResume(AppException.class,
                        e -> Mono.just(new ApiResponseDTO(e.getErrorCode()))
                );
    }

    public Mono<ApiResponseDTO> updateDevice(DeviceDTO dto, String role, Integer id) {
        return deviceRepo.findById(id) //check NOT_FOUND first since only used by ADMIN/IT
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.NOT_FOUND)))
                .flatMap(existing -> validateDevice(dto)
                        .flatMap(d -> deviceTypeRepo.findByNameAndManagedBy(d.getType(), role)
                                .switchIfEmpty(Mono.error(new AppException(ErrorCode.INVALID_TYPE)))
                                .flatMap(deviceType -> {
                                    existing.setName(d.getName());
                                    existing.setTypeId(deviceType.getId());
                                    return deviceRepo.save(existing);
                                })
                        )
                )
                .map(updated -> new ApiResponseDTO(updated.getId()))
                .onErrorResume(AppException.class,
                        e -> Mono.just(new ApiResponseDTO(e.getErrorCode()))
                );
    }


}
