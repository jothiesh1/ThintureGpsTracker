package com.GpsTracker.Thinture.service;

import com.GpsTracker.Thinture.dto.LocationUpdate;
import com.GpsTracker.Thinture.model.GpsData;
import com.GpsTracker.Thinture.model.Vehicle;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/*
**********************************Developer Jothiesh **********************
*                                                                         /
*                                 ********************                                                                  /
***************************************************************************
*                                                                         /
*
*                                                                         /
*
***************************************************************************
*
*/
@Service
public class MqttService {

    private static final Logger logger = LoggerFactory.getLogger(MqttService.class);

    @Autowired
    private MqttClient mqttClient;

    @Value("${mqtt.topic}")
    private String topic;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private VehicleService vehicleService;

    private final List<String> receivedData = new CopyOnWriteArrayList<>();

    public void publish(String message) {
        try {
            MqttMessage mqttMessage = new MqttMessage(message.getBytes(StandardCharsets.UTF_8));
            mqttMessage.setQos(2);
            mqttClient.publish(topic, mqttMessage);
            logger.info("Published message: {}", message);
        } catch (MqttException e) {
            logger.error("Error while publishing message: {}", e.getMessage(), e);
        }
    }

    @PostConstruct
    public void subscribe() {
        try {
            mqttClient.subscribe(topic, (topic, message) -> {
                String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
                logger.info("Raw payload received: {}", payload);

                try {
                    String processedPayload = isHexadecimal(payload) ? hexToAscii(payload) : payload;
                    logger.info("Processed payload: {}", processedPayload);

                    processPayload(processedPayload);
                } catch (Exception e) {
                    logger.error("Failed to process payload: {}", payload, e);
                }
            });
            logger.info("Subscribed to topic: {}", topic);
        } catch (MqttException e) {
            logger.error("Error while subscribing to topic: {}", e.getMessage(), e);
        }
    }

    private void processPayload(String payload) {
        try {
            GpsData gpsData = objectMapper.readValue(payload, GpsData.class);

            // Check if the device is registered
            boolean isRegistered = vehicleService.getAllVehicles().stream()
                    .anyMatch(vehicle -> vehicle.getDeviceID().equals(gpsData.getDeviceID()));

            if (isRegistered) {
                double latitude = parseCoordinate(gpsData.getLatitude());
                double longitude = parseCoordinate(gpsData.getLongitude());

                gpsData.setLatitude(String.valueOf(latitude));
                gpsData.setLongitude(String.valueOf(longitude));

                logger.info("Processed GPS Data: {}", gpsData);

                messagingTemplate.convertAndSend("/topic/location-updates",
                        new LocationUpdate(latitude, longitude, gpsData.getDeviceID(), gpsData.getTimestamp()));
            } else {
                logger.warn("Received data for unregistered device ID: {}", gpsData.getDeviceID());
            }
        } catch (Exception e) {
            logger.error("Failed to parse payload: {}", payload, e);
        }
    }

    private double parseCoordinate(String coordinate) {
        char direction = coordinate.charAt(coordinate.length() - 1);
        double value = Double.parseDouble(coordinate.substring(0, coordinate.length() - 1));

        if (direction == 'S' || direction == 'W') {
            value = -value;
        }

        return value;
    }

    private String hexToAscii(String hexStr) {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < hexStr.length(); i += 2) {
            String str = hexStr.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }
        return output.toString();
    }

    private boolean isHexadecimal(String payload) {
        return payload.matches("\\p{XDigit}+");
    }

    public List<String> getReceivedData() {
        return new CopyOnWriteArrayList<>(receivedData);
    }
}
