package com.example.report.service;

public interface ReportService {

    byte[] generateDevicesReportPDF(boolean landscape,String userId, String authHeader);

    byte[] generateActiveDevicesReportPDF(boolean landscape, String userId, String authHeader);

    byte[] generateDevicesReportCSV(String userId, String authHeader);

    byte[] generateActiveDevicesReportCSV(String userId, String authHeader);
}
