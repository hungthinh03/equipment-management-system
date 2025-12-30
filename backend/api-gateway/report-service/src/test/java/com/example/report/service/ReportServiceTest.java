package com.example.report.service;

import com.example.report.client.DeviceClient;
import com.example.report.dto.DeviceDTO;
import com.example.report.repository.ReportRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {
    @Mock
    private ReportRepository reportRepo;

    @Mock
    private DeviceClient deviceClient;

    @InjectMocks
    private ReportServiceImpl reportService;

    private static final String userId = "1";
    private static final String authHeader = "bearer-token";

    @Test
    void generateDevicesReportPDF_success() {
        when(deviceClient.getAllDevices(anyString()))
                .thenReturn(Mono.just(List.of(new DeviceDTO())));

        byte[] pdfBytes = reportService.generateDevicesReportPDF(true, userId, authHeader);

        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);

        verify(reportRepo, times(1)).save(any());
    }

    @Test
    void generateActiveDevicesReportPDF_success() {
        when(deviceClient.getAllActiveDevices(anyString()))
                .thenReturn(Mono.just(List.of(new DeviceDTO())));

        byte[] pdfBytes = reportService.generateActiveDevicesReportPDF(true, userId, authHeader);

        assertNotNull(pdfBytes);
        assertTrue(pdfBytes.length > 0);

        verify(reportRepo, times(1)).save(any());
    }

    @Test
    void generateDevicesReportCSV_success() {
        when(deviceClient.getAllDevices(anyString()))
                .thenReturn(Mono.just(List.of(new DeviceDTO())));

        byte[] csvBytes = reportService.generateDevicesReportCSV(userId, authHeader);

        assertNotNull(csvBytes);
        assertTrue(csvBytes.length > 0);

        String csvOutput = new String(csvBytes, StandardCharsets.UTF_8);
        assertTrue(csvOutput.contains("ID,Name,Type,Serial Number,Manufacturer"));

        verify(reportRepo, times(1)).save(any());
    }

    @Test
    void generateActiveDevicesReportCSV_success() {
        when(deviceClient.getAllActiveDevices(anyString()))
                .thenReturn(Mono.just(List.of(new DeviceDTO())));

        byte[] csvBytes = reportService.generateActiveDevicesReportCSV(userId, authHeader);

        assertNotNull(csvBytes);
        assertTrue(csvBytes.length > 0);

        String csvOutput = new String(csvBytes, StandardCharsets.UTF_8);
        assertTrue(csvOutput.contains("ID,Name,Type,Serial Number,Manufacturer"));

        verify(reportRepo, times(1)).save(any());
    }

}