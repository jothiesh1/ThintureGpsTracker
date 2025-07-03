package com.GpsTracker.Thinture.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.GpsTracker.Thinture.dto.ParkingReportDTO;
import com.GpsTracker.Thinture.dto.VehicleReportDTO;
import com.GpsTracker.Thinture.repository.VehicleReportRepository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.util.List;
import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VehicleReportService {

    @Autowired
    private VehicleReportRepository vehicleReportRepository;

    private static final Logger logger = LoggerFactory.getLogger(VehicleReportService.class);

    /**
     * Fetches vehicle reports based on filters.
     */
    public List<VehicleReportDTO> getVehicleReport(Timestamp startDate, Timestamp endDate, String deviceId, String vehicleStatus) {
        logger.info("Fetching vehicle reports with startDate={}, endDate={}, deviceId={}, vehicleStatus={}", startDate, endDate, deviceId, vehicleStatus);

        // Fetch data from repository
        List<Object[]> results = vehicleReportRepository.findReports(startDate, endDate, deviceId, vehicleStatus);

        if (results == null || results.isEmpty()) {
            logger.info("No reports found for the given filters.");
            return Collections.emptyList();
        }

        logger.info("Fetched {} records from the database.", results.size());
        return results.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    /**
     * Maps database result to DTO.
     */
    private VehicleReportDTO mapToDTO(Object[] row) {
        VehicleReportDTO dto = new VehicleReportDTO();
        dto.setDeviceID(row[0] != null ? row[0].toString() : null);
        dto.setLatitude(row[1] != null ? ((Number) row[1]).doubleValue() : null);
        dto.setLongitude(row[2] != null ? ((Number) row[2]).doubleValue() : null);
        dto.setVehicleSpeed(row[3] != null ? ((Number) row[3]).doubleValue() : null);
        dto.setTimestamp(row[4] != null ? (Timestamp) row[4] : null);
        dto.setIgnition(row[5] != null ? row[5].toString() : null);
        dto.setVehicleStatus(row[6] != null ? row[6].toString() : null);
        return dto;
    }

    /**
     * Exports vehicle reports to a PDF file.
     */
    public String exportReportsToPDF(List<VehicleReportDTO> reports, String outputPath) {
        logger.info("Starting PDF report generation for outputPath: {}", outputPath);

        if (reports.isEmpty()) {
            logger.warn("No data available to generate the PDF report.");
            throw new IllegalArgumentException("No data available to generate the PDF report.");
        }

        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(outputPath));
            document.open();

            // Add Title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("Vehicle Reports", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" ")); // Blank line for spacing

            // Create table
            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);

            Stream.of("Device ID", "Timestamp", "Latitude", "Longitude", "Speed", "Ignition", "Vehicle Status")
                    .forEach(column -> {
                        PdfPCell header = new PdfPCell(new Phrase(column));
                        header.setHorizontalAlignment(Element.ALIGN_CENTER);
                        header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                        table.addCell(header);
                    });

            // Populate rows
            for (VehicleReportDTO report : reports) {
                table.addCell(report.getDeviceID() != null ? report.getDeviceID() : "N/A");
                table.addCell(report.getTimestamp() != null ? report.getTimestamp().toString() : "N/A");
                table.addCell(report.getLatitude() != null ? report.getLatitude().toString() : "N/A");
                table.addCell(report.getLongitude() != null ? report.getLongitude().toString() : "N/A");
                table.addCell(report.getVehicleSpeed() != null ? report.getVehicleSpeed().toString() + " km/h" : "0 km/h");
                table.addCell(report.getIgnition() != null ? report.getIgnition() : "N/A");
                table.addCell(report.getVehicleStatus() != null ? report.getVehicleStatus() : "N/A");
            }

            document.add(table);
            document.close();

            logger.info("PDF report successfully generated at: {}", outputPath);
            return "PDF report generated successfully at: " + outputPath;

        } catch (Exception e) {
            logger.error("Error generating PDF report: {}", e.getMessage(), e);
            throw new RuntimeException("Error generating PDF report: " + e.getMessage(), e);
        }
    }

    /**
     * Exports vehicle reports to an Excel file.
     */
    public String exportReportsToExcel(List<VehicleReportDTO> reports, String outputPath) {
        logger.info("Starting Excel report generation for outputPath: {}", outputPath);

        if (reports.isEmpty()) {
            logger.warn("No data available to generate the Excel report.");
            throw new IllegalArgumentException("No data available to generate the Excel report.");
        }

        try (Workbook workbook = new XSSFWorkbook();
             FileOutputStream fileOut = new FileOutputStream(outputPath)) {

            Sheet sheet = workbook.createSheet("Vehicle Reports");

            // Header
            Row header = sheet.createRow(0);
            String[] columns = {"Device ID", "Timestamp", "Latitude", "Longitude", "Speed", "Ignition", "Vehicle Status"};

            for (int i = 0; i < columns.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(columns[i]);
                CellStyle style = workbook.createCellStyle();
                org.apache.poi.ss.usermodel.Font font = workbook.createFont();
                font.setBold(true);
                style.setFont(font);
                cell.setCellStyle(style);
            }

            // Populate rows
            int rowNum = 1;
            for (VehicleReportDTO report : reports) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(report.getDeviceID() != null ? report.getDeviceID() : "N/A");
                row.createCell(1).setCellValue(report.getTimestamp() != null ? report.getTimestamp().toString() : "N/A");
                row.createCell(2).setCellValue(report.getLatitude() != null ? report.getLatitude().toString() : "N/A");
                row.createCell(3).setCellValue(report.getLongitude() != null ? report.getLongitude().toString() : "N/A");
                row.createCell(4).setCellValue(report.getVehicleSpeed() != null ? report.getVehicleSpeed().toString() : "N/A");
                row.createCell(5).setCellValue(report.getIgnition() != null ? report.getIgnition() : "N/A");
                row.createCell(6).setCellValue(report.getVehicleStatus() != null ? report.getVehicleStatus() : "N/A");
            }

            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(fileOut);
            logger.info("Excel report successfully generated at: {}", outputPath);
            return "Excel report generated successfully at: " + outputPath;

        } catch (IOException e) {
            logger.error("Error generating Excel report: {}", e.getMessage(), e);
            throw new RuntimeException("Error generating Excel report: " + e.getMessage(), e);
        }
    }
    
    
    
    
    
    
    
}



/*
        List<VehicleReportDTO> reportList = new ArrayList<>();
        
        for (Object[] row : result) {
            VehicleReportDTO report = new VehicleReportDTO();
            logger.debug("Mapping result row: {}", Arrays.toString(row));

            report.setDeviceID((String) row[0]);
            report.setVehicleNumber((String) row[1]);
            report.setVehicleType((String) row[2]);
            report.setOwnerName((String) row[3]);
            report.setEngineNumber((String) row[4]);
            report.setManufacturer((String) row[5]);
            report.setModel((String) row[6]);
            report.setInstallationDate((Date) row[7]);
            report.setSerialNo((String) row[8]);
            report.setTechnicianName((String) row[9]);
            report.setImei((String) row[10]);
            report.setSimNumber((String) row[11]);
            report.setDealerName((String) row[12]);
            report.setAddressPhone((String) row[13]);
            report.setCountry((String) row[14]);
            report.setLatitude((Double) row[15]);
            report.setLongitude((Double) row[16]);
            report.setTimestamp((Timestamp) row[17]);
            report.setVehicleSpeed((Double) row[18]);
            report.setCourse((String) row[19]);
            report.setAdditionalData((String) row[20]);
            report.setSequenceNumber((Integer) row[21]);
            report.setViolationDate((Date) row[22]);
            report.setViolationType((String) row[23]);
            report.setViolationSpeed((String) row[24]);
            report.setLocation((String) row[25]);
            report.setStatus((String) row[26]);

            reportList.add(report);
        }

        logger.info("Returning {} records for DeviceID: {}", reportList.size(), deviceID);
        return reportList;
    }
    
    */

