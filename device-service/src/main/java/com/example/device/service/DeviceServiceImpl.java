package com.example.device.service;

import com.example.device.common.enums.ErrorCode;
import com.example.device.common.exception.AppException;
import com.example.device.dto.ApiResponseDTO;
import com.example.device.dto.AddDeviceDTO;
import com.example.device.dto.DeviceResponseDTO;
import com.example.device.dto.SearchResponseDTO;
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

    private Mono<AddDeviceDTO> validateDevice(AddDeviceDTO dto) {
        return Mono.justOrEmpty(dto) // stream of elements ["a", "b", "c"]
                .filter(d -> Stream.of(d.getName(), d.getType()).allMatch(Objects::nonNull))
                .filter(d -> Stream.of(d.getName(), d.getType()).noneMatch(String::isBlank))
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.INVALID_INPUT)));
    }

    //
    public Mono<ApiResponseDTO> addDevice(AddDeviceDTO dto, String role) {
        return validateDevice(dto)
                .flatMap(d -> deviceTypeRepo.findByNameAndManagedBy(d.getType(), role)
                        .switchIfEmpty(Mono.error(new AppException(ErrorCode.INVALID_TYPE)))
                )
                .flatMap(deviceType -> deviceRepo.save(new Device(dto, deviceType.getId())))
                .map(savedDevice -> new ApiResponseDTO(savedDevice.getId()));
    }

    public Mono<ApiResponseDTO> updateDevice(AddDeviceDTO dto, String role, Integer id) {
        return deviceRepo.findById(id) //can check NOT_FOUND first since only used by ADMIN/IT
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
                .map(updated -> new ApiResponseDTO(updated.getId()));
    }

    public Mono<DeviceResponseDTO> viewDevice(String role, Integer id) {
        return deviceRepo.findByIdAndManagedBy(id, role)
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.INACCESSIBLE)))
                .map(DeviceResponseDTO::new);
    }

    public Mono<DeviceResponseDTO> viewAllDevices(String role) {
        return deviceRepo.findAllByManagedBy(role)
                .collectList()
                .map(DeviceResponseDTO::new); // Only return list of devices managed by current user
    }

    public Mono<SearchResponseDTO> searchDevices(String name, String type) {
        return deviceRepo.searchByParameter(name, type)
                .collectList()
                .map(SearchResponseDTO::new);
    }



}
