package com.GpsTracker.Thinture.controller;
///
import com.GpsTracker.Thinture.model.GpsData;
import com.GpsTracker.Thinture.service.MqttService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/** ࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏࿏
 * Author: Jothiesh ☃☃☃☃☃☃☃
 *  Developer, R&D 
 * ⁂⁂⁂⁂⁂⁂⁂⁂⁂⁂⁂⁂⁂⁂⁂⁂⁂⁂⁂⁂⁂⁂⁂⁂⁂
 * 
 */
@RestController
@RequestMapping("/api/mqtt")
public class MqttController {

    private static final Logger logger = LoggerFactory.getLogger(MqttController.class);

    @Autowired
    private MqttService mqttService;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping("/publish/{topic}")
    public String publish(@PathVariable String topic, @RequestBody String message) {
        try {
            GpsData gpsData;
            if (message.startsWith("{")) {
                gpsData = objectMapper.readValue(message, GpsData.class);
            } else {
                gpsData = new GpsData();
                gpsData.setAdditionalData(message);
            }
            logger.info("Received request to publish GPS data to topic {}: {}", topic, gpsData);

            String jsonPayload = objectMapper.writeValueAsString(gpsData);
            logger.info("Publishing JSON payload to topic {}: {}", topic, jsonPayload);

            mqttService.publish(topic, jsonPayload);
            logger.info("Published GPS data successfully to topic {}: {}", topic, gpsData);
            return "GPS data published to topic " + topic + ": " + gpsData.toString();
        } catch (Exception e) {
            logger.error("Error while parsing GPS data: {}", e.getMessage(), e);
            return "Error while parsing GPS data: " + e.getMessage();
        }
    }
}
