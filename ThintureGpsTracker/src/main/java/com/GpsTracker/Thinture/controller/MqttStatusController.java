package com.GpsTracker.Thinture.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.GpsTracker.Thinture.service.MqttService;

@RestController
@RequestMapping("/api/mqtt")
public class MqttStatusController {

    @Autowired
    private MqttService mqttService;

    @GetMapping("/status")
    public ResponseEntity<?> getMqttStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("connected", mqttService.isConnected());
        status.put("lastMessageTimestamp", mqttService.getLastMessageTimestamp());
        return ResponseEntity.ok(status);
    }
}