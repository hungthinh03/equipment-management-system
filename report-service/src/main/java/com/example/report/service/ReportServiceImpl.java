package com.example.report.service;

import com.example.report.client.DeviceClient;
import com.example.report.dto.DeviceDTO;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

@Service
public class ReportServiceImpl implements ReportService {
    private final DeviceClient deviceClient;

    Color background = new Color(48, 144, 255, 173);

    @Autowired
    public ReportServiceImpl(DeviceClient deviceClient) {
        this.deviceClient = deviceClient;
    }

    private void addTableHeader(PdfPTable table) {
        Stream.of("ID", "Name", "Type", "Serial", "Manufacturer", "Ownership", "Created At", "Status")
                .forEach(header -> {
                    PdfPCell cell = new PdfPCell(new Phrase(
                            header, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.WHITE)));
                    cell.setBackgroundColor(background);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setBorderColor(background); // border same color
                    table.addCell(cell);
                });
    }

    private void addRows(PdfPTable table, List<DeviceDTO> devices) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                .withZone(ZoneId.systemDefault());

        for (DeviceDTO device : devices) {
            String[] values = {
                    String.valueOf(device.getId()),
                    device.getName(),
                    device.getType(),
                    device.getSerialNumber(),
                    device.getManufacturer(),
                    device.getOwnershipType(),
                    device.getCreatedAt() != null ? formatter.format(device.getCreatedAt()) : "-",
                    device.getStatus()
            };

            for (int i = 0; i < values.length; i++) {
                PdfPCell cell = new PdfPCell(new Phrase(values[i]));
                cell.setBorderColor(background); // light blue border
                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                cell.setPadding(5f);

                if (i == 1) { // Name column
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                } else {
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                }
                table.addCell(cell);
            }
        }
    }

    public List<DeviceDTO> getAllDevices(String authHeader) {
        return deviceClient.getAllDevices(authHeader).block();
    }

    public List<DeviceDTO> getAllActiveDevices(String authHeader) {
        return deviceClient.getAllDevices(authHeader).block();
    }

    private byte[] buildDevicesReportPDF(List<DeviceDTO> devices, boolean landscape) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Document document = new Document(landscape ? PageSize.A4.rotate() : PageSize.A4);
        PdfWriter.getInstance(document, outputStream);

        document.open();

        // Title
        Paragraph title = new Paragraph(
                "Device Inventory Report",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20));
        //title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        // Metadata
        Paragraph meta = new Paragraph();
        meta.add("Generated on:\t" + LocalDate.now() + "\n");
        meta.add("Record count:\t" + (devices == null ? 0 : devices.size() + 1));
        document.add(meta);
        document.add(Chunk.NEWLINE);

        // Table
        PdfPTable table = new PdfPTable(8);
        table.setWidthPercentage(100);

        float[] columnWidths = {0.75f, 3.75f, 1.5f, 2f, 2f, 2f, 1.75f, 2.25f};
        table.setWidths(columnWidths);

        addTableHeader(table);
        addRows(table, devices);

        document.add(table);
        document.close();
        return outputStream.toByteArray();
    }

    public byte[] generateDevicesReportPDF(boolean landscape, String authHeader) {
        return buildDevicesReportPDF(getAllDevices(authHeader), landscape);
    }

    public byte[] generateActiveDevicesReportPDF(boolean landscape, String authHeader) {
        return buildDevicesReportPDF(getAllActiveDevices(authHeader), landscape);
    }

    private byte[] buildDevicesReportCSV(List<DeviceDTO> devices) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);

        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader("ID", "Name", "Type", "Serial Number", "Manufacturer",
                        "Ownership", "Created At", "Status")
                .setSkipHeaderRecord(false)
                .build();

        try (CSVPrinter csvPrinter = new CSVPrinter(writer, format)) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            for (DeviceDTO device : devices) {
                csvPrinter.printRecord(
                        device.getId(),
                        device.getName(),
                        device.getType(),
                        device.getSerialNumber(),
                        device.getManufacturer(),
                        device.getOwnershipType(),
                        device.getCreatedAt() != null
                                ? formatter.format(device.getCreatedAt().atZone(ZoneOffset.UTC))
                                : "-",
                        device.getStatus()
                );
            }
            csvPrinter.flush();

        } catch (IOException e) {
            throw new RuntimeException("Error generating CSV report", e);
        }
        return outputStream.toByteArray();
    }

    public byte[] generateDevicesReportCSV(String authHeader) {
        return buildDevicesReportCSV(getAllDevices(authHeader));
    }

    public byte[] generateActiveDevicesReportCSV(String authHeader) {
        return buildDevicesReportCSV(getAllActiveDevices(authHeader));
    }
}
