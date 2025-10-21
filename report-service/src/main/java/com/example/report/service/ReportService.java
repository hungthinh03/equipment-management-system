package com.example.report.service;

public interface ReportService {

    byte[] generateAllDevicesReportPDF(boolean landscape, String authHeader);
}
