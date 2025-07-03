package com.GpsTracker.Thinture.controller;



import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/logs")
public class LogController {

    private final String LOG_FILE_PATH = "logs/app.log";  // ✅ Path to your log file

    // 🔹 1. Get full logs
    @GetMapping("/all")
    @PreAuthorize("hasRole('SUPERADMIN') or hasRole('ADMIN')")
    public ResponseEntity<String> getAllLogs() {
        try {
            Path logPath = Paths.get(LOG_FILE_PATH);
            String content = Files.readString(logPath);
            return ResponseEntity.ok(content);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error reading logs: " + e.getMessage());
        }
    }

    // 🔹 2. Search logs by keyword (optional: server-side search)
    @GetMapping("/search")
    @PreAuthorize("hasRole('SUPERADMIN') or hasRole('ADMIN')")
    public ResponseEntity<List<String>> searchLogs(@RequestParam String keyword) {
        try {
            Path logPath = Paths.get(LOG_FILE_PATH);
            List<String> matched = Files.lines(logPath)
                    .filter(line -> line.toLowerCase().contains(keyword.toLowerCase()))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(matched);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonList("Error searching logs."));
        }
    }

    // 🔹 3. Download logs by date range
    @GetMapping("/download")
    @PreAuthorize("hasRole('SUPERADMIN') or hasRole('ADMIN')")
    public ResponseEntity<Resource> downloadLogsByDate(
            @RequestParam String startDate,
            @RequestParam String endDate) {

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDate start = LocalDate.parse(startDate, formatter);
            LocalDate end = LocalDate.parse(endDate, formatter);

            Path logPath = Paths.get(LOG_FILE_PATH);

            List<String> filteredLogs = Files.lines(logPath)
                    .filter(line -> {
                        try {
                            String datePart = line.substring(0, 10);
                            LocalDate logDate = LocalDate.parse(datePart, formatter);
                            return (logDate.equals(start) || logDate.isAfter(start)) &&
                                   (logDate.equals(end) || logDate.isBefore(end));
                        } catch (Exception e) {
                            return false;  // Skip lines that don't have a valid date
                        }
                    }).collect(Collectors.toList());

            // ✅ Create temp file with filtered logs
            Path tempFile = Files.createTempFile("filtered-logs-", ".log");
            Files.write(tempFile, filteredLogs);

            Resource resource = new UrlResource(tempFile.toUri());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=logs_" + startDate + "_to_" + endDate + ".log")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
