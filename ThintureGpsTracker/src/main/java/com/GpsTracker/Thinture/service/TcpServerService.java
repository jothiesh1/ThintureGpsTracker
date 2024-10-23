package com.GpsTracker.Thinture.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import com.GpsTracker.Thinture.dto.LocationUpdate;
import com.GpsTracker.Thinture.model.GpsData;
import com.GpsTracker.Thinture.model.VehicleHistory;
import com.GpsTracker.Thinture.model.VehicleLastLocation;
import com.GpsTracker.Thinture.repository.VehicleLastLocationRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Timestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

@Service
public class TcpServerService {

    private static final Logger logger = LoggerFactory.getLogger(TcpServerService.class);

    @Value("${tcp.server.port}")
    private int tcpPort;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private VehicleHistoryService vehicleHistoryService;

    @Autowired
    private VehicleLastLocationRepository vehicleLastLocationRepository;

    @PostConstruct
    public void startServer() {
        try (ServerSocket serverSocket = new ServerSocket(tcpPort)) {
            logger.info("TCP Server started on port {}", tcpPort);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                logger.info("Client connected: {}", clientSocket.getInetAddress());

                BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
                String receivedData = input.readLine();

                if (receivedData != null) {
                    logger.info("Raw payload received: {}", receivedData);
                    processPayload(receivedData);
                }

                clientSocket.close();
            }
        } catch (Exception e) {
            logger.error("Error while running the TCP server", e);
        }
    }

    /**
     * Process the incoming GPS data payload and update the database
     */
    private void processPayload(String payload) {
        try {
            // Clean the payload to remove non-breaking spaces and other non-ASCII characters
            String cleanedPayload = payload.replaceAll("[^\\x00-\\x7F]", "");
            logger.info("Cleaned payload: {}", cleanedPayload);

            GpsData gpsData = objectMapper.readValue(cleanedPayload, GpsData.class);

            // Validate essential fields
            if (gpsData.getDeviceID() == null || gpsData.getLatitude() == null ||
                gpsData.getLongitude() == null || gpsData.getTimestamp() == null ||
                gpsData.getStatus() == null) { // Add validation for status
                logger.error("Invalid GPS data received: {}", gpsData);
                return;
            }

            // Process and save GPS data
            vehicleService.getVehicleByDeviceID(gpsData.getDeviceID()).ifPresentOrElse(vehicle -> {
                double latitude = parseCoordinate(gpsData.getLatitude());
                double longitude = parseCoordinate(gpsData.getLongitude());

                // Save to vehicle_history table
                VehicleHistory history = new VehicleHistory();
                history.setVehicle(vehicle); // Set the vehicle entity
                history.setTimestamp(Timestamp.valueOf(gpsData.getTimestamp()));
                history.setLatitude(latitude);
                history.setLongitude(longitude);
                history.setSpeed(Double.valueOf(gpsData.getSpeed()));
                history.setCourse(gpsData.getCourse());
                history.setAdditionalData(gpsData.getAdditionalData());
                history.setSequenceNumber(Integer.valueOf(gpsData.getSequenceNumber()));

                vehicleHistoryService.saveVehicleHistory(history);
                logger.info("Processed GPS Data and saved to history: {}", gpsData);

                // Update the last known location
                VehicleLastLocation lastLocation = vehicleLastLocationRepository.findByDeviceId(gpsData.getDeviceID())
                    .orElse(new VehicleLastLocation());

                lastLocation.setDeviceId(gpsData.getDeviceID());
                lastLocation.setLatitude(latitude);
                lastLocation.setLongitude(longitude);
                lastLocation.setTimestamp(Timestamp.valueOf(gpsData.getTimestamp()));
                lastLocation.setStatus(gpsData.getStatus()); // Ensure you set the status correctly

                logger.info("Updating last location with status: {} for device ID: {}", gpsData.getStatus(), gpsData.getDeviceID());
                vehicleLastLocationRepository.save(lastLocation);
                logger.info("Updated last known location for device ID: {}", gpsData.getDeviceID());

                // Send location update to the frontend
                messagingTemplate.convertAndSend("/topic/location-updates",
                        new LocationUpdate(latitude, longitude, gpsData.getDeviceID(), gpsData.getTimestamp()));

            }, () -> {
                logger.error("Vehicle with deviceID {} not found in the database.", gpsData.getDeviceID());
            });
        } catch (Exception e) {
            logger.error("Failed to parse or process payload: {}", payload, e);
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
}

