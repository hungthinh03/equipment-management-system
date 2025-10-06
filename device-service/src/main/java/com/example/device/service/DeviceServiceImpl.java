package com.example.device.service;

import com.example.device.common.enums.ErrorCode;
import com.example.device.common.exception.AppException;
import com.example.device.dto.*;
import com.example.device.model.Device;
import com.example.device.repository.DeviceRepository;
import com.example.device.repository.DeviceTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;


@Service
public class DeviceServiceImpl implements DeviceService {
    @Autowired
    private DeviceRepository deviceRepo;

    @Autowired
    private DeviceTypeRepository deviceTypeRepo;

    private Mono<AddDeviceDTO> validateDevice(AddDeviceDTO dto) {
        return Mono.justOrEmpty(dto)
                .filter(d -> Stream.of(
                                d.getName(),
                                d.getType(),
                                d.getSerialNumber(),
                                d.getManufacturer(),
                                d.getPurchaseDate()
                        ).allMatch(s -> s != null && !s.isBlank())
                                && d.getPurchasePrice() != null
                )
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.MISSING_FIELDS)));
    }

    private boolean isValidPurchaseDate(String date) {
        try {
            parseDateAsInstant(date);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private Instant parseDateAsInstant(String date) {
        if (date == null || date.isBlank())
            return null; //optional
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate parsed = LocalDate.parse(date, fmt);
        return parsed.atStartOfDay(ZoneOffset.UTC).toInstant();
    }

    private Mono<Void> validateSerialNumber(String serialNumber) {
        return deviceRepo.findBySerialNumber(serialNumber)
                .flatMap(existing -> Mono.error(new AppException(ErrorCode.DUPLICATE_SERIAL)))
                .then(); // proceed
    }

    public Mono<ApiResponse> addDevice(AddDeviceDTO dto, String userId, String role) {
        return validateDevice(dto)
                .filter(d -> isValidPurchaseDate(dto.getPurchaseDate()))
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.INVALID_DATE)))
                .flatMap(d -> deviceTypeRepo.findByNameAndManagedBy(d.getType(), role)
                        .switchIfEmpty(Mono.error(new AppException(ErrorCode.INVALID_TYPE)))
                        .flatMap(deviceType ->
                                validateSerialNumber(d.getSerialNumber())
                                        .then(Mono.just(
                                                new Device(d, deviceType.getId(),
                                                        parseDateAsInstant(d.getPurchaseDate()), Integer.valueOf(userId))))
                        )
                )
                .flatMap(deviceRepo::save)
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

    private Mono<UUID> validateUuid(String uuid) {
        try {
            return Mono.just(UUID.fromString(uuid));
        } catch (IllegalArgumentException e) {
            return Mono.error(new AppException(ErrorCode.INVALID_UUID));
        }
    }

    public Mono<SearchResponse> viewDeviceByUuid(String uuid) {
        return validateUuid(uuid)
                .flatMap(deviceRepo::searchByUuid)
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.NOT_FOUND)))
                .map(SearchResponse::new);
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

    public Mono<ApiResponse> updateDeviceAssignment(UpdateStatusDTO dto, String role, String uuid) {
        return validateUuid(uuid)
                .flatMap(deviceRepo::findByUuid)
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.NOT_FOUND)))
                .filter(existing -> List.of("AVAILABLE", "ASSIGNED").contains(dto.getStatus()))
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.INVALID_STATUS)))
                .flatMap(existing -> {
                    existing.setStatus(dto.getStatus());
                    existing.setAssignedTo(dto.getAssignedTo());
                    return deviceRepo.save(existing);
                })
                .map(updated -> new ApiResponse(updated.getId()));
    }


}
