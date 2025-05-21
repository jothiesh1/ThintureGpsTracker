package com.GpsTracker.Thinture.controller;

import org.apache.commons.io.input.ReversedLinesFileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.GpsTracker.Thinture.dto.CommandRequest;
import com.GpsTracker.Thinture.service.MqttService;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ota")
public class OtaCommandController {

    private static final Logger logger = LoggerFactory.getLogger(OtaCommandController.class);

    @Autowired
    private MqttService mqttService;

    @Autowired
    private ResourceLoader resourceLoader;

    // ✅ Publish OTA command via MQTT
    @PostMapping("/send-command")
    public ResponseEntity<Map<String, String>> sendCommand(@RequestBody CommandRequest request) {
        long startTime = System.currentTimeMillis();
        Map<String, String> response = new HashMap<>();
        try {
            String mobileNumber = request.getMobileNumber();
            String deviceId = request.getDeviceId();
            String command = request.getCommand();
            String value = request.getValue();

            String fullCommand = (value != null && !value.isEmpty()) ? command + ":" + value : command;
            String topic = "device/" + deviceId;

            logger.info("[OTA] Sending command to device. Mobile: {}, DeviceID: {}, Command: {}",
                    mobileNumber, deviceId, fullCommand);

            mqttService.publish(topic, fullCommand);

            response.put("status", "Command published successfully");
            logger.info("[OTA] Command published in {} ms", System.currentTimeMillis() - startTime);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("[OTA-ERROR] Error publishing command: {}", e.getMessage(), e);
            response.put("status", "Error publishing command");
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // ✅ OTA version check endpoint (public)
    @GetMapping(value = "/firmware/update_check", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> checkUpdate(
            @RequestParam("id") String deviceId,
            @RequestParam("version") String clientVersion) {
        long startTime = System.currentTimeMillis();
        try {
            String latestVersion = readLatestVersionFromClasspath();
            boolean shouldUpdate = isOlderVersion(clientVersion, latestVersion);

            logger.info("[OTA] Device {} requested update check. Current: {}, Latest: {}, Update Required: {}",
                    deviceId, clientVersion, latestVersion, shouldUpdate);
            logger.info("[OTA] Version check completed in {} ms", System.currentTimeMillis() - startTime);
            return ResponseEntity.ok(shouldUpdate);
        } catch (Exception e) {
            logger.error("[OTA-ERROR] Failed to check update for {}: {}", deviceId, e.getMessage());
            return ResponseEntity.internalServerError().body(false);
        }
    }

    // ✅ Reads version line like ;VERSION=V1.0.3 from resources/static/firmware/main.hex
    private String readLatestVersionFromClasspath() throws IOException {
        long startTime = System.currentTimeMillis();
        Resource resource = resourceLoader.getResource("classpath:static/firmware/main.hex");

        File hexFile = resource.getFile();
        logger.info("[OTA] Reading firmware version from file: {}", hexFile.getAbsolutePath());

        try (ReversedLinesFileReader reader = new ReversedLinesFileReader(hexFile, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith(";VERSION=")) {
                    String version = line.replace(";VERSION=", "").trim();
                    logger.info("[OTA] ✅ Found version: {} in {} ms", version, System.currentTimeMillis() - startTime);
                    return version.startsWith("V") ? version : "V" + version;
                }
            }
        }
        throw new IOException("❌ Version line not found in main.hex");
    }

    // ✅ Compares version strings numerically, including V1.02 vs V1.2
    private boolean isOlderVersion(String clientVersion, String latestVersion) {
        long startTime = System.currentTimeMillis();
        String[] clientParts = clientVersion.replace("V", "").split("\\.");
        String[] latestParts = latestVersion.replace("V", "").split("\\.");

        int maxLength = Math.max(clientParts.length, latestParts.length);
        for (int i = 0; i < maxLength; i++) {
            int clientVal = (i < clientParts.length) ? parseIntSafe(clientParts[i]) : 0;
            int latestVal = (i < latestParts.length) ? parseIntSafe(latestParts[i]) : 0;

            if (clientVal < latestVal) {
                logger.info("[OTA] Version comparison took {} ms: update required.", System.currentTimeMillis() - startTime);
                return true;   // client is older
            }
            if (clientVal > latestVal) {
                logger.info("[OTA] Version comparison took {} ms: update not required.", System.currentTimeMillis() - startTime);
                return false;  // client is newer
            }
        }
        logger.info("[OTA] Version comparison took {} ms: versions are equal.", System.currentTimeMillis() - startTime);
        return false; // Equal
    }

    // ✅ Helper to parse "02" as 2 safely
    private int parseIntSafe(String str) {
        try {
            return Integer.parseInt(str.replaceAll("^0+", ""));
        } catch (NumberFormatException e) {
            logger.warn("[OTA] Failed to parse version segment '{}'. Defaulting to 0.", str);
            return 0;
        }
    }

}
