package com.example.device.controller;

import com.example.device.common.enums.ErrorCode;
import com.example.device.common.exception.AppException;
import com.example.device.dto.AddDeviceDTO;
import com.example.device.dto.ViewDeviceDTO;
import com.example.device.response.ApiResponse;
import com.example.device.response.DeviceResponse;
import com.example.device.response.SearchResponse;
import com.example.device.response.TypeResponse;
import com.example.device.service.DeviceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@WebFluxTest(Controller.class)
@Import(DeviceControllerTest.TestConfig.class)
class DeviceControllerTest {
    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private DeviceService deviceService; // Spring-managed mock

    @TestConfiguration
    static class TestConfig {
        @Bean
        DeviceService deviceService() {
            return mock(DeviceService.class); // Mockito mock
        }
    }

    private static final String userId = "1";
    private static final String role = "ADMIN";

    @Test
    void addDevice_success() {
        AddDeviceDTO dto = new AddDeviceDTO(
                "Dell XPS 15",
                "Laptop",
                "SN-DEL-1001",
                "Dell",
                new BigDecimal("1800.00"),
                "2023-08-14"
        );

        // Mock service response
        when(deviceService.addDevice(any(AddDeviceDTO.class), anyString(), anyString()))
                .thenReturn(Mono.just(new ApiResponse(1)));

        // Perform POST request
        webTestClient.post()
                .uri("/device")
                .header("X-User-Id", userId)
                .header("X-User-Role", role)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ApiResponse.class)
                .value(response -> {
                    assertEquals("success", response.getStatus());
                    assertEquals(1, response.getId());
                });
    }

    private void testAddDeviceError(AddDeviceDTO dto, AppException error, HttpStatus expectedStatus, int expectedCode) {
        when(deviceService.addDevice(any(AddDeviceDTO.class), anyString(), anyString()))
                .thenReturn(Mono.error(error));

        webTestClient.post()
                .uri("/device")
                .header("X-User-Id", userId)
                .header("X-User-Role", role)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus)
                .expectBody(ApiResponse.class)
                .value(response -> {
                    assertEquals("error", response.getStatus());
                    assertEquals(expectedCode, response.getStatusCode());
                });
    }

    @Test
    void addDevice_fail_missingFields() {
        AddDeviceDTO dto = new AddDeviceDTO(
                "",
                "Laptop",
                "SN-DEL-1001",
                "Dell",
                new BigDecimal("1800.00"),
                "2023-08-14"
        );

        testAddDeviceError(
                dto,
                new AppException(ErrorCode.MISSING_FIELDS),
                HttpStatus.BAD_REQUEST,
                1003
        );
    }

    @Test
    void addDevice_fail_invalidDate() {
        AddDeviceDTO dto = new AddDeviceDTO(
                "Dell XPS 15",
                "Laptop",
                "SN-DEL-1001",
                "Dell",
                new BigDecimal("1800.00"),
                "2023/14/8"
        );

        testAddDeviceError(
                dto,
                new AppException(ErrorCode.INVALID_DATE),
                HttpStatus.BAD_REQUEST,
                1014
        );
    }

    @Test
    void addDevice_fail_duplicateSerial() {
        AddDeviceDTO dto = new AddDeviceDTO(
                "Dell XPS 15",
                "Laptop",
                "SN-DEL-1001",
                "Dell",
                new BigDecimal("1800.00"),
                "2023-08-14"
        );

        testAddDeviceError(
                dto,
                new AppException(ErrorCode.DUPLICATE_SERIAL),
                HttpStatus.CONFLICT,
                1015
        );
    }

    @Test
    void addDevice_fail_typeNotFound() {
        AddDeviceDTO dto = new AddDeviceDTO(
                "Dell XPS 15",
                "Cabbage",
                "SN-DEL-1001",
                "Dell",
                new BigDecimal("1800.00"),
                "2023-08-14"
        );

        testAddDeviceError(
                dto,
                new AppException(ErrorCode.TYPE_NOT_FOUND),
                HttpStatus.NOT_FOUND,
                1016
        );
    }

    @Test
    void addDevice_fail_typeInaccessible() {
        AddDeviceDTO dto = new AddDeviceDTO(
                "Cisco Switch 2960",
                "Switch",
                "SN-CIS-1001",
                "Cisco",
                new BigDecimal("2500.00"),
                "2023-08-14"
        );

        testAddDeviceError(
                dto,
                new AppException(ErrorCode.INACCESSIBLE_TYPE),
                HttpStatus.FORBIDDEN,
                1006
        );
    }

    @Test
    void updateDevice_success() {
        Integer id = 1;
        AddDeviceDTO dto = new AddDeviceDTO(
                "Dell XPS 15",
                "Laptop",
                "SN-DEL-1001",
                "Dell",
                new BigDecimal("1800.00"),
                "2023-08-14"
        );

        when(deviceService.updateDevice(any((AddDeviceDTO.class)), any(), any(), eq(id)))
                .thenReturn(Mono.just(new ApiResponse(id)));

        webTestClient.put()
                .uri("/device/{id}", id)
                .header("X-User-Id", userId)
                .header("X-User-Role", role)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ApiResponse.class)
                .value(response -> {
                    assertEquals("success", response.getStatus());
                    assertEquals(1, response.getId());
                });
    }

    private void testUpdateDeviceError(AddDeviceDTO dto, Integer id, ErrorCode error,
                                       HttpStatus expectedStatus, int expectedCode) {
        when(deviceService.updateDevice(any((AddDeviceDTO.class)), any(), any(), eq(id)))
                .thenReturn(Mono.error(new AppException(error)));

        webTestClient.put()
                .uri("/device/{id}", id)
                .header("X-User-Id", userId)
                .header("X-User-Role", role)
                .bodyValue(dto)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus)
                .expectBody(ApiResponse.class)
                .value(response -> {
                    assertEquals("error", response.getStatus());
                    assertEquals(expectedCode, response.getStatusCode());
                });
    }

    @Test
    void updateDevice_fail_notFound() {
        Integer id = 1;
        AddDeviceDTO dto = new AddDeviceDTO();

        testUpdateDeviceError(
                dto,
                id,
                ErrorCode.NOT_FOUND,
                HttpStatus.NOT_FOUND,
                1007
        );
    }

    @Test
    void updateDevice_fail_invalidOperation() {
        Integer id = 1;
        AddDeviceDTO dto = new AddDeviceDTO();

        testUpdateDeviceError(
                dto,
                id,
                ErrorCode.INVALID_OPERATION,
                HttpStatus.FORBIDDEN,
                1017
        );
    }

    @Test
    void updateDevice_fail_missingFields() {
        Integer id = 1;
        AddDeviceDTO dto = new AddDeviceDTO();

        testUpdateDeviceError(
                dto,
                id,
                ErrorCode.MISSING_FIELDS,
                HttpStatus.BAD_REQUEST,
                1003
        );
    }

    @Test
    void updateDevice_fail_invalidDate() {
        Integer id = 1;
        AddDeviceDTO dto = new AddDeviceDTO();

        testUpdateDeviceError(
                dto,
                id,
                ErrorCode.INVALID_DATE,
                HttpStatus.BAD_REQUEST,
                1014
        );
    }

    @Test
    void updateDevice_fail_duplicateSerial() {
        Integer id = 1;
        AddDeviceDTO dto = new AddDeviceDTO();

        testUpdateDeviceError(
                dto,
                id,
                ErrorCode.DUPLICATE_SERIAL,
                HttpStatus.CONFLICT,
                1015
        );
    }

    @Test
    void viewDevice_success() {
        Integer id = 1;
        ViewDeviceDTO deviceDTO = new ViewDeviceDTO();
        when(deviceService.viewDevice(any(), eq(id)))
                .thenReturn(Mono.just(new DeviceResponse(deviceDTO)));

        webTestClient.get()
                .uri("/device/{id}", id)
                .header("X-User-Role", role)
                .exchange()
                .expectStatus().isOk()
                .expectBody(DeviceResponse.class)
                .value(response -> assertEquals("success", response.getStatus()));
    }

    private void testViewDeviceError(Integer id, ErrorCode error, HttpStatus expectedStatus, int expectedCode) {
        when(deviceService.viewDevice(any(), eq(id)))
                .thenReturn(Mono.error(new AppException(error)));

        webTestClient.get()
                .uri("/device/{id}", id)
                .header("X-User-Role", role)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus)
                .expectBody(ApiResponse.class)
                .value(response -> {
                    assertEquals("error", response.getStatus());
                    assertEquals(expectedCode, response.getStatusCode());
                });
    }

    @Test
    void viewDevice_fail_notFound() {
        Integer id = 1;

        testViewDeviceError(
                id,
                ErrorCode.NOT_FOUND,
                HttpStatus.NOT_FOUND,
                1007
        );
    }

    @Test
    void viewDevice_fail_inaccessible() {
        Integer id = 1;

        testViewDeviceError(
                id,
                ErrorCode.INACCESSIBLE,
                HttpStatus.FORBIDDEN,
                1008
        );
    }

    @Test
    void viewAllDevices_success() {
        when(deviceService.viewAllDevices(any()))
                .thenReturn(Mono.just(new DeviceResponse(List.of())));

        webTestClient.get()
                .uri("/device")
                .header("X-User-Role", role)
                .exchange()
                .expectStatus().isOk()
                .expectBody(DeviceResponse.class)
                .value(response -> assertEquals("success", response.getStatus()));
    }

    @Test
    void viewAllDeviceTypes_success() {
        when(deviceService.viewAllDeviceTypes(any()))
                .thenReturn(Mono.just(new TypeResponse(List.of())));

        webTestClient.get()
                .uri("/device/type")
                .header("X-User-Role", role)
                .exchange()
                .expectStatus().isOk()
                .expectBody(DeviceResponse.class)
                .value(response -> assertEquals("success", response.getStatus()));
    }

    @Test
    void searchDevices_success() {
        when(deviceService.searchDevices(any(), any(), anyInt()))
                .thenReturn(Mono.just(new SearchResponse(List.of(), 1, 10, 1)));

        webTestClient.get()
                .uri("/device/search")
                .exchange()
                .expectStatus().isOk()
                .expectBody(SearchResponse.class)
                .value(response -> assertEquals("success", response.getStatus()));
    }

    @Test
    void decommissionDevice_success() {
        Integer id = 1;
        when(deviceService.decommissionDevice(any(), any(), eq(id)))
                .thenReturn(Mono.just(new ApiResponse(1)));

        webTestClient.delete()
                .uri("/device/{id}", id)
                .header("X-User-Id", userId)
                .header("X-User-Role", role)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ApiResponse.class)
                .value(response -> assertEquals("success", response.getStatus()));
    }

    private void testDecommissionDeviceError(Integer id, ErrorCode error, HttpStatus expectedStatus, int expectedCode) {
        when(deviceService.decommissionDevice(any(), any(), eq(id)))
                .thenReturn(Mono.error(new AppException(error)));

        webTestClient.delete()
                .uri("/device/{id}", id)
                .header("X-User-Id", userId)
                .header("X-User-Role", role)
                .exchange()
                .expectStatus().isEqualTo(expectedStatus)
                .expectBody(ApiResponse.class)
                .value(response -> {
                    assertEquals("error", response.getStatus());
                    assertEquals(expectedCode, response.getStatusCode());
                });
    }

    @Test
    void decommissionDevice_fail_notFound() {
        Integer id = 1;

        testDecommissionDeviceError(
                id,
                ErrorCode.NOT_FOUND,
                HttpStatus.NOT_FOUND,
                1007
        );
    }

    @Test
    void decommissionDevice_fail_inaccessible() {
        Integer id = 1;

        testDecommissionDeviceError(
                id,
                ErrorCode.INACCESSIBLE,
                HttpStatus.FORBIDDEN,
                1008
        );
    }

    @Test
    void decommissionDevice_fail_invalidOperation() {
        Integer id = 1;

        testDecommissionDeviceError(
                id,
                ErrorCode.INVALID_OPERATION,
                HttpStatus.FORBIDDEN,
                1017
        );
    }
}