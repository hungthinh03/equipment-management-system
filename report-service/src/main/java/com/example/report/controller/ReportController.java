package com.example.report.controller;

import com.example.report.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/report")
public class ReportController {
    @Autowired
    private ReportService reportService;

    @GetMapping("/device")
    public ResponseEntity<byte[]> generateAllDevicesReportPDF(@RequestHeader("Authorization") String authHeader) {
        byte[] pdfBytes = reportService.generateDevicesReportPDF(false, authHeader);
        return ResponseEntity.ok()
                .header("Content-Disposition",
                        "attachment; filename=devices_report.pdf")
                .body(pdfBytes);
    }

    @GetMapping("/device/active")
    public ResponseEntity<byte[]> generateActiveDevicesReportPDF(@RequestHeader("Authorization") String authHeader) {
        byte[] pdfBytes = reportService.generateActiveDevicesReportPDF(false, authHeader);
        return ResponseEntity.ok()
                .header("Content-Disposition",
                        "attachment; filename=active_devices_report.pdf")
                .body(pdfBytes);
    }
}
