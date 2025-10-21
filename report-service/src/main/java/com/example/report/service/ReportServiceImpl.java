package com.example.report.service;

import com.example.report.client.DeviceClient;
import com.example.report.dto.DeviceDTO;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
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
                    cell.setBorderColor(background); // border same color
                    table.addCell(cell);
                });
    }

    private String safe(String value) {
        return value != null ? value : "-";
    }

    private void addRows(PdfPTable table, List<DeviceDTO> devices) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                .withZone(ZoneId.systemDefault());

        for (DeviceDTO device : devices) {
            Stream.of(
                    String.valueOf(device.getId()),
                    device.getName(),
                    device.getType(),
                    device.getSerialNumber(),
                    device.getManufacturer(),
                    device.getOwnershipType(),
                    device.getCreatedAt() != null ? formatter.format(device.getCreatedAt()) : "-",
                    device.getStatus()
            ).forEach(value -> {
                PdfPCell cell = new PdfPCell(new Phrase(value));
                cell.setBorderColor(background); // light blue border
                table.addCell(cell);
            });
        }
    }



    private byte[] buildDevicesReportPDF(List<DeviceDTO> devices, boolean landscape) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Document document = new Document(landscape ? PageSize.A4.rotate() : PageSize.A4);
        PdfWriter.getInstance(document, outputStream);

        document.open();

        // Title
        Paragraph title = new Paragraph(
                "Device Inventory Report",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16));
        //title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        // Metadata
        Paragraph meta = new Paragraph();
        meta.add("Generated on:\t" + LocalDate.now() + "\n");
        meta.add("Record count:\t" + (devices == null ? 0 : devices.size() + 1));
        document.add(meta);
        //document.add(Chunk.NEWLINE);

        // Table
        PdfPTable table = new PdfPTable(8);
        table.setWidthPercentage(100);

        addTableHeader(table);
        addRows(table, devices);

        document.add(table);
        document.close();

        return outputStream.toByteArray();
    }

    public byte[] generateDevicesReportPDF(boolean landscape, String authHeader) {
        List<DeviceDTO> devices = deviceClient.getAllDevices(authHeader).block();
        return buildDevicesReportPDF(devices, landscape);
    }

    public byte[] generateActiveDevicesReportPDF(boolean landscape, String authHeader) {
        List<DeviceDTO> devices = deviceClient.getAllActiveDevices(authHeader).block();
        return buildDevicesReportPDF(devices, landscape);
    }
}
