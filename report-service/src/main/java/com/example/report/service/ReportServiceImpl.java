package com.example.report.service;

import com.example.report.dto.DeviceDTO;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
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

    private void addTableHeader(PdfPTable table) {
        Stream.of("ID", "Name", "Type", "Serial", "Manufacturer", "Ownership", "Created At", "Retired At")
                .forEach(column -> {
                    PdfPCell header = new PdfPCell(new Phrase(column,
                            FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
                    header.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(header);
                });
    }

    private String safe(String value) {
        return value != null ? value : "-";
    }

    private void addRows(PdfPTable table, List<DeviceDTO> devices) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                .withZone(ZoneId.systemDefault());

        for (DeviceDTO d : devices) {
            table.addCell(String.valueOf(d.getId()));
            table.addCell(safe(d.getName()));
            table.addCell(safe(d.getType()));
            table.addCell(safe(d.getSerialNumber()));
            table.addCell(safe(d.getManufacturer()));
            table.addCell(safe(d.getOwnershipType()));
            table.addCell(d.getCreatedAt() != null ? formatter.format(d.getCreatedAt()) : "-");
            table.addCell(d.getRetiredAt() != null ? formatter.format(d.getRetiredAt()) : "-");
        }
    }


    public byte[] generateAllDevicesReportPDF(List<DeviceDTO> devices, boolean landscape) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Document document = new Document(landscape ? PageSize.A4.rotate() : PageSize.A4);
        PdfWriter.getInstance(document, outputStream);

        document.open();

        // Title
        Paragraph title = new Paragraph(
                "Device Inventory Report",
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16));
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        //
        document.add(new Paragraph("Generated on: " + LocalDate.now()));
        document.add(new Paragraph(" "));

        // Table
        PdfPTable table = new PdfPTable(8);
        table.setWidthPercentage(100);

        addTableHeader(table);
        addRows(table, devices);

        document.add(table);
        document.close();

        return outputStream.toByteArray();
    }


}
