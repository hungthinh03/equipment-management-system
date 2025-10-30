package com.example.report.controller;

import com.example.report.service.ReportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReportController.class)
@Import(ReportControllerTest.TestConfig.class)
class ReportControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReportService reportService; // Spring-managed mock

    @TestConfiguration
    static class TestConfig {
        @Bean
        ReportService reportService() {
            return mock(ReportService.class); // Mockito mock
        }
    }

    private static final String userId = "1";
    private static final String authHeader = "bearer-token";

    @Test
    void generateAllDevicesReportPDF_success() throws Exception {
        byte[] expectedPdfBytes = "bytes-pdf".getBytes();
        when(reportService.generateDevicesReportPDF(anyBoolean(), any(), any()))
                .thenReturn(expectedPdfBytes);

        mockMvc.perform(get("/report/device/pdf")
                        .header("X-User-Id", userId)
                        .header("Authorization", authHeader))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition",
                        "attachment; filename=devices_report.pdf"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(content().bytes(expectedPdfBytes));
    }

    @Test
    void generateActiveDevicesReportPDF_success() throws Exception {
        byte[] expectedPdfBytes = "bytes-pdf".getBytes();
        when(reportService.generateActiveDevicesReportPDF(anyBoolean(), any(), any()))
                .thenReturn(expectedPdfBytes);

        mockMvc.perform(get("/report/device/active/pdf")
                        .header("X-User-Id", userId)
                        .header("Authorization", authHeader))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition",
                        "attachment; filename=active_devices_report.pdf"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(content().bytes(expectedPdfBytes));
    }

    @Test
    void generateDevicesReportCSV_success() throws Exception {
        byte[] expectedCsvBytes = "bytes-csv".getBytes();
        when(reportService.generateDevicesReportCSV(any(), any()))
                .thenReturn(expectedCsvBytes);

        mockMvc.perform(get("/report/device/csv")
                        .header("X-User-Id", userId)
                        .header("Authorization", authHeader))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition",
                        "attachment; filename=devices_report.csv"))
                .andExpect(content().contentType(MediaType.parseMediaType("text/csv")))
                .andExpect(content().bytes(expectedCsvBytes));
    }

    @Test
    void generateActiveDevicesReportCSV_success() throws Exception {
        byte[] expectedCsvBytes = "bytes-csv".getBytes();
        when(reportService.generateActiveDevicesReportCSV(any(), any()))
                .thenReturn(expectedCsvBytes);

        mockMvc.perform(get("/report/device/active/csv")
                        .header("X-User-Id", userId)
                        .header("Authorization", authHeader))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition",
                        "attachment; filename=active_devices_report.csv"))
                .andExpect(content().contentType(MediaType.parseMediaType("text/csv")))
                .andExpect(content().bytes(expectedCsvBytes));
    }
}