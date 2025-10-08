package com.example.device.service;

import com.example.device.common.enums.ErrorCode;
import com.example.device.common.exception.AppException;
import com.example.device.dto.*;
import com.example.device.model.Device;
import com.example.device.model.DeviceType;
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
            return null;
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate parsed = LocalDate.parse(date, fmt);
        return parsed.atStartOfDay(ZoneOffset.UTC).toInstant();
    }

    private Mono<Void> validateSerialNumber(String serialNumber) {
        return deviceRepo.findBySerialNumber(serialNumber)
                .flatMap(existing -> Mono.error(new AppException(ErrorCode.DUPLICATE_SERIAL)))
                .then(); // proceed
    }

    private Mono<DeviceType> validateDeviceType(String name, String role) {
        return deviceTypeRepo.findByName(name)
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.TYPE_NOT_FOUND)))
                .flatMap(dt -> deviceTypeRepo.findByNameAndManagedBy(name, role)
                        .switchIfEmpty(Mono.error(new AppException(ErrorCode.INACCESSIBLE_TYPE)))
                );
    }

    public Mono<ApiResponse> addDevice(AddDeviceDTO dto, String userId, String role) {
        return validateDevice(dto)
                .filter(d -> isValidPurchaseDate(dto.getPurchaseDate()))
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.INVALID_DATE)))
                .flatMap(d -> validateDeviceType(d.getType(), role)
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

    private Mono<Void> validateUpdatedSerialNumber(String newSerial, String oldSerial) {
        return Mono.just(newSerial)
                .filter(s -> !s.equals(oldSerial))
                .flatMap(this::validateSerialNumber) // check if new serial number already exists
                .then();
    }

    public Mono<ApiResponse> updateDevice(AddDeviceDTO dto, String userId, String role, Integer id) {
        return deviceRepo.findById(id) // check NOT_FOUND first since only used by ADMIN/IT
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.NOT_FOUND)))
                .filter(device -> "AVAILABLE".equalsIgnoreCase(device.getStatus()) ||
                        "MAINTENANCE".equalsIgnoreCase(device.getStatus()))
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.INVALID_OPERATION)))
                .flatMap(existing -> validateDevice(dto)
                        .flatMap(d -> validateDeviceType(d.getType(), role)
                                .flatMap(deviceType ->
                                        validateUpdatedSerialNumber(d.getSerialNumber(), existing.getSerialNumber())
                                                .then(Mono.defer(() -> {
                                                    existing.setName(d.getName());
                                                    existing.setTypeId(deviceType.getId());
                                                    existing.setSerialNumber(d.getSerialNumber());
                                                    existing.setManufacturer(d.getManufacturer());
                                                    existing.setPurchasePrice(d.getPurchasePrice());
                                                    existing.setPurchaseDate(parseDateAsInstant(d.getPurchaseDate()));
                                                    existing.setUpdatedBy(Integer.valueOf(userId));
                                                    return deviceRepo.save(existing);
                                                }))
                                )
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

    public Mono<TypeResponse> viewAllDeviceTypes(String role) {
        return deviceTypeRepo.findAllNamesByManagedBy(role)
                .collectList()
                .defaultIfEmpty(List.of())
                .map(TypeResponse::new);
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

    public Mono<ApiResponse> updateDeviceMaintenance(Boolean maintenance, String userId, String role, Integer id) {
        return Mono.justOrEmpty(maintenance)
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.MISSING_FIELDS)))
                .then(deviceRepo.findById(id)
                        .switchIfEmpty(Mono.error(new AppException(ErrorCode.NOT_FOUND)))
                        .then(deviceRepo.findDeviceByIdAndManagedBy(id, role)
                                .switchIfEmpty(Mono.error(new AppException(ErrorCode.INACCESSIBLE))))
                        .filter(device ->
                                maintenance && "AVAILABLE".equals(device.getStatus()) ||
                                !maintenance && "MAINTENANCE".equals(device.getStatus()))
                        .switchIfEmpty(Mono.error(new AppException(ErrorCode.INVALID_OPERATION)))
                        .flatMap(device -> {
                            device.setStatus(maintenance ? "MAINTENANCE" : "AVAILABLE");
                            device.setUpdatedBy(Integer.valueOf(userId));
                            return deviceRepo.save(device);
                        })
                        .map(saved -> new ApiResponse(saved.getId())));
    }

    public Mono<ApiResponse> decommissionDevice(String userId, String role, Integer id) {
        return deviceRepo.findById(id)
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.NOT_FOUND)))
                .then(deviceRepo.findDeviceByIdAndManagedBy(id, role)
                        .switchIfEmpty(Mono.error(new AppException(ErrorCode.INACCESSIBLE))))
                .filter(device -> "AVAILABLE".equals(device.getStatus())
                        || "MAINTENANCE".equals(device.getStatus()))
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.INVALID_OPERATION)))
                .flatMap(device -> {
                    device.setStatus("DECOMMISSIONED");
                    device.setUpdatedBy(Integer.valueOf(userId));
                    return deviceRepo.save(device);
                })
                .map(updated -> new ApiResponse(updated.getId()));
    }

    public Mono<ApiResponse> updateDeviceAssignment(UpdateStatusDTO dto, String userId, String uuid) {
        return validateUuid(uuid)
                .flatMap(deviceRepo::findByUuid)
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.NOT_FOUND)))
                .filter(device -> !"DECOMMISSIONED".equals(device.getStatus()))
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.INVALID_OPERATION)))
                .filter(existing -> List.of("AVAILABLE", "ASSIGNED", "RESERVED").contains(dto.getStatus()))
                .switchIfEmpty(Mono.error(new AppException(ErrorCode.INVALID_STATUS)))
                .flatMap(existing -> {
                    existing.setStatus(dto.getStatus());
                    existing.setAssignedTo(dto.getAssignedTo());
                    existing.setUpdatedBy(Integer.valueOf(userId));
                    return deviceRepo.save(existing);
                })
                .map(updated -> new ApiResponse(updated.getId()));
    }


}
