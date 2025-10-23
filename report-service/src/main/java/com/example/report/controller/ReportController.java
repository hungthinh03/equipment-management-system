package com.example.report.controller;

import com.example.report.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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

    @GetMapping("/device/pdf")
    public ResponseEntity<byte[]> generateAllDevicesReportPDF(@RequestHeader("X-User-Id") String userId,
                                                              @RequestHeader("Authorization") String authHeader) {
        byte[] pdfBytes = reportService.generateDevicesReportPDF(true, userId, authHeader);
        return ResponseEntity.ok()
                .header("Content-Disposition",
                        "attachment; filename=devices_report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @GetMapping("/device/active/pdf")
    public ResponseEntity<byte[]> generateActiveDevicesReportPDF(@RequestHeader("X-User-Id") String userId,
                                                                 @RequestHeader("Authorization") String authHeader) {
        byte[] pdfBytes = reportService.generateActiveDevicesReportPDF(true, userId, authHeader);
        return ResponseEntity.ok()
                .header("Content-Disposition",
                        "attachment; filename=active_devices_report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @GetMapping("/device/csv")
    public ResponseEntity<byte[]> generateDevicesReportCSV(@RequestHeader("X-User-Id") String userId,
                                                           @RequestHeader("Authorization") String authHeader) {
        byte[] csvBytes = reportService.generateDevicesReportCSV(userId, authHeader);
        return ResponseEntity.ok()
                .header("Content-Disposition",
                        "attachment; filename=devices_report.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvBytes);
    }

    @GetMapping("/device/active/csv")
    public ResponseEntity<byte[]> generateActiveDevicesReportCSV(@RequestHeader("X-User-Id") String userId,
                                                                 @RequestHeader("Authorization") String authHeader) {
        byte[] csvBytes = reportService.generateActiveDevicesReportCSV(userId, authHeader);
        return ResponseEntity.ok()
                .header("Content-Disposition",
                        "attachment; filename=active_devices_report.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvBytes);
    }
}
