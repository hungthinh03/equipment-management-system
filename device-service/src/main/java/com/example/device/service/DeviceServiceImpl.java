package com.example.device.service;

import com.example.device.common.enums.ErrorCode;
import com.example.device.common.exception.AppException;
import com.example.device.dto.ApiResponse;
import com.example.device.dto.AddDeviceDTO;
import com.example.device.dto.DeviceResponse;
import com.example.device.dto.SearchResponse;
import com.example.device.model.Device;
import com.example.device.repository.DeviceRepository;
import com.example.device.repository.DeviceTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
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
    public Mono<ApiResponse> addDevice(AddDeviceDTO dto, String role) {
        return validateDevice(dto)
                .flatMap(d -> deviceTypeRepo.findByNameAndManagedBy(d.getType(), role)
                        .switchIfEmpty(Mono.error(new AppException(ErrorCode.INVALID_TYPE)))
                )
                .flatMap(deviceType -> deviceRepo.save(new Device(dto, deviceType.getId())))
                .map(savedDevice -> new ApiResponse(savedDevice.getId()));
    }

    public Mono<ApiResponse> updateDevice(AddDeviceDTO dto, String role, Integer id) {
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
                .map(updated -> new ApiResponse(updated.getId()));
    }

    public Mono<DeviceResponse> viewDevice(String role, Integer id) {
        return deviceRepo.findById(id)
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.NOT_FOUND)))
                .then(deviceRepo.findViewByIdAndManagedBy(id, role)
                        .switchIfEmpty(Mono.error(new AppException(ErrorCode.INACCESSIBLE))))
                .map(DeviceResponse::new);
    }

    public Mono<DeviceResponse> viewAllDevices(String role) {
        return deviceRepo.findAllByManagedBy(role)
                .collectList()
                .map(DeviceResponse::new); // Only return list of devices managed by current user
    }

    private boolean isBlankParams(String name, String type) {
        return (name == null || name.isBlank()) &&
                (type == null || type.isBlank());
    }

    public Mono<SearchResponse> searchDevices(String name, String type) {
        return Mono.defer(() ->
                isBlankParams(name, type)
                        ? Mono.just(new SearchResponse(List.of()))
                        : deviceRepo.searchByParameter(name, type)
                        .collectList()
                        .map(SearchResponse::new)
        );
    }

    public Mono<ApiResponse> decommissionDevice(String role, Integer id) {
        return deviceRepo.findById(id)
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.NOT_FOUND)))
                .then(deviceRepo.findDeviceByIdAndManagedBy(id, role)
                        .switchIfEmpty(Mono.error(new AppException(ErrorCode.INACCESSIBLE))))
                .flatMap(device -> {
                    device.setStatus("DECOMMISSIONED");
                    return deviceRepo.save(device);
                })
                .map(updated -> new ApiResponse(updated.getId()));
    }

}
