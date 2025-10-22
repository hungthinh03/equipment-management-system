package com.example.report.service;

public interface ReportService {

    byte[] generateDevicesReportPDF(boolean landscape, String authHeader);

    byte[] generateActiveDevicesReportPDF(boolean landscape, String authHeader);

    byte[] generateDevicesReportCSV(String authHeader);

    byte[] generateActiveDevicesReportCSV(String authHeader);
}
