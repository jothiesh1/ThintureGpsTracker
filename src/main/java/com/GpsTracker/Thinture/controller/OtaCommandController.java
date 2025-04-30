package com.GpsTracker.Thinture.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.GpsTracker.Thinture.dto.CommandRequest;
import com.GpsTracker.Thinture.service.MqttService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ota")
public class OtaCommandController {

    private static final Logger logger = LoggerFactory.getLogger(OtaCommandController.class);

    @Autowired
    private MqttService mqttService;

    @PostMapping("/send-command")
    public ResponseEntity<Map<String, String>> sendCommand(@RequestBody CommandRequest request) {
        Map<String, String> response = new HashMap<>();
        try {
            String mobileNumber = request.getMobileNumber();
            String deviceId = request.getDeviceId();
            String command = request.getCommand();
            String value = request.getValue();

            String fullCommand = (value != null && !value.isEmpty()) ? command + ":" + value : command;
            String topic = "device/" + deviceId;

            logger.info("[OTA] Sending command to device. Mobile: {}, DeviceID: {}, Command: {}", mobileNumber, deviceId, fullCommand);

            mqttService.publish(topic, fullCommand);

            response.put("status", "Command published successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            logger.error("[OTA-ERROR] Error publishing command: {}", e.getMessage(), e);
            response.put("status", "Error publishing command");
            return ResponseEntity.internalServerError().body(response);
        }
    }
    
}