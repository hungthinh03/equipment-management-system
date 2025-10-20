package com.example.report.service;

import com.example.report.dto.DeviceDTO;

import java.util.List;

public interface ReportService {

    byte[] generateAllDevicesReportPDF(List<DeviceDTO> devices, boolean landscape);
}
