package com.GpsTracker.Thinture.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.GpsTracker.Thinture.dto.LocationUpdate;
import com.GpsTracker.Thinture.model.GpsData;
import com.GpsTracker.Thinture.model.Vehicle;
import com.GpsTracker.Thinture.model.VehicleHistory;
import com.GpsTracker.Thinture.model.VehicleLastLocation;
import com.GpsTracker.Thinture.repository.VehicleLastLocationRepository;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;






@Service
public class MqttService {

    private static final Logger logger = LoggerFactory.getLogger(MqttService.class);

    @Autowired
    private MqttClient mqttClient;

    @Value("${mqtt.topics}")
    private String[] topics;

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

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_CYAN = "\u001B[36m";

    private final ExecutorService executorService = Executors.newFixedThreadPool(10); 
    private final List<VehicleHistory> historyBatch = Collections.synchronizedList(new ArrayList<>());
    private static final int BATCH_SIZE = 100;

    @PostConstruct
    public void subscribe() {
        try {
            for (String topic : topics) {
                if (!topic.endsWith("#") && topic.contains("#")) {
                    logger.error("\u001B[31m[MQTT] Invalid topic: {}. Multi-level wildcard (#) can only appear at the end.\u001B[0m", topic);
                    continue;
                }

                mqttClient.subscribe(topic.trim(), (t, message) -> {
                    String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
                    logger.info("\u001B[34m[MQTT] Received Payload on {}: {}\u001B[0m", t, payload);

                    executorService.submit(() -> {
                        try {
                            String processedPayload = isHexadecimal(payload) ? hexToAscii(payload) : payload;
                            processPayload(processedPayload);
                        } catch (Exception e) {
                            logger.error("\u001B[31m[ERROR] Failed to process payload on topic {}: {}\u001B[0m", t, payload, e);
                        }
                    });
                });
                logger.info("\u001B[32m[MQTT] Subscribed to topic: {}\u001B[0m", topic);
            }

            // 🔥 Additional Subscription: Device Command Response Subscription
            mqttClient.subscribe("device/response/#", (topic, message) -> {
                String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
                logger.info("\u001B[36m[MQTT-RESPONSE] Received device response on {}: {}\u001B[0m", topic, payload);

                // Push the device response to WebSocket (for frontend to display)
                messagingTemplate.convertAndSend("/topic/device-responses", payload);
            });
            logger.info("\u001B[32m[MQTT] Subscribed to device/response/# topic for command responses\u001B[0m");

        } catch (MqttException e) {
            logger.error("\u001B[31m[MQTT] Error while subscribing: {}\u001B[0m", e.getMessage(), e);
        }
    }


    public void publish(String topic, String message) {
        try {
            MqttMessage mqttMessage = new MqttMessage(message.getBytes(StandardCharsets.UTF_8));
            mqttMessage.setQos(2);
            mqttClient.publish(topic, mqttMessage);
            logger.info("\u001B[36m[MQTT] Published message to {}: {}\u001B[0m", topic, message);
        } catch (MqttException e) {
            logger.error("\u001B[31m[MQTT] Error while publishing to {}: {}\u001B[0m", topic, e.getMessage(), e);
        }
    }

    /**
     * Process incoming GPS data payload (handles both single & batch).
     */
    private void processPayload(String payload) {
        try {
            String cleanedPayload = payload.replaceAll("[^\\x00-\\x7F]", ""); 
            logger.info("\u001B[36m[INFO] Cleaned payload: {}\u001B[0m", cleanedPayload);

            // Attempt to parse as a JSON array
            if (cleanedPayload.trim().startsWith("[")) {
                // Handle as JSON Array
                List<GpsData> gpsDataList = objectMapper.readValue(cleanedPayload, new TypeReference<List<GpsData>>() {});
                logger.info("\u001B[32m[INFO] Processing batch of {} GPS records\u001B[0m", gpsDataList.size());
                gpsDataList.forEach(this::processGpsData);
            } else {
                // Handle single JSON object
                GpsData gpsData = objectMapper.readValue(cleanedPayload, GpsData.class);
                logger.info("\u001B[34m[INFO] Processing single GPS record for Device ID: {}\u001B[0m", gpsData.getDeviceID());
                processGpsData(gpsData);
            }
        } catch (JsonProcessingException e) {
            logger.error("\u001B[31m[ERROR] JSON Parsing failed: {}\u001B[0m", payload, e);
        } catch (Exception e) {
            logger.error("\u001B[31m[ERROR] Unexpected error while processing payload: {}\u001B[0m", payload, e);
        }
    }

    
    
    
  // :CODE IMEI
    private void processGpsData(GpsData gpsData) {
        try {
            // ✅ Step 1: Validate essential data
            if (gpsData == null ||
                gpsData.getDeviceID() == null ||
                gpsData.getLatitude() == null ||
                gpsData.getLongitude() == null ||
                gpsData.getTimestamp() == null ||
                gpsData.getStatus() == null) {
                logger.error("\u001B[31m[ERROR] Invalid GPS data received: {}\u001B[0m", gpsData);
                return;
            }

            String deviceID = gpsData.getDeviceID();
            String imei = gpsData.getImei();

            logger.info("[INFO] Processing GPS Data: deviceID={}, IMEI={}", deviceID, imei);

            // ✅ Step 2: Check if IMEI is present AND deviceID is valid
            if (imei != null && !imei.trim().isEmpty() && deviceID != null && !deviceID.trim().isEmpty()) {
                Optional<Vehicle> imeiVehicleOpt = vehicleService.getVehicleByImei(imei.trim());

                if (imeiVehicleOpt.isPresent()) {
                    Vehicle vehicle = imeiVehicleOpt.get();
                    String existingDeviceID = vehicle.getDeviceID();

                    if (existingDeviceID == null || existingDeviceID.trim().isEmpty()) {
                        // 👉 IMEI found but deviceID is empty ➔ store deviceID
                        vehicle.setDeviceID(deviceID.trim());
                        vehicleService.save(vehicle);
                        logger.info("\u001B[32m[UPDATE] Vehicle IMEI {}: set new deviceID to {}\u001B[0m", imei, deviceID);
                    } else if (!existingDeviceID.equalsIgnoreCase(deviceID.trim())) {
                        // ⚠️ Conflict case: IMEI has a different deviceID already
                        logger.warn("\u001B[33m[WARN] IMEI {} already linked to deviceID {} (incoming deviceID: {})\u001B[0m",
                                imei, existingDeviceID, deviceID);
                    } else {
                        // ✅ Already correct
                        logger.info("\u001B[34m[INFO] IMEI {} already linked to deviceID {} - no update needed\u001B[0m", imei, existingDeviceID);
                    }
                } else {
                    // 🚩 No vehicle found with this IMEI
                    logger.error("\u001B[31m[ERROR] No vehicle found with IMEI {} (cannot update deviceID)\u001B[0m", imei);
                }
            } else {
                logger.info("\u001B[33m[INFO] Skipping IMEI check: IMEI or deviceID missing (IMEI={}, deviceID={})\u001B[0m", imei, deviceID);
            }

            // ✅ Step 3: Lookup by deviceID to process history + last location
            Optional<Vehicle> vehicleOpt = vehicleService.getVehicleByDeviceID(deviceID.trim());

            if (vehicleOpt.isPresent()) {
                Vehicle vehicle = vehicleOpt.get();
                double latitude = Double.parseDouble(gpsData.getLatitude());
                double longitude = Double.parseDouble(gpsData.getLongitude());

                // Save history
                VehicleHistory history = new VehicleHistory();
                history.setVehicle(vehicle);
                history.setTimestamp(Timestamp.valueOf(gpsData.getTimestamp()));
                history.setLatitude(latitude);
                history.setLongitude(longitude);
                history.setSpeed(Double.valueOf(gpsData.getSpeed()));
                history.setCourse(gpsData.getCourse());
                history.setSequenceNumber(gpsData.getSequenceNumber());
                history.setIgnition(gpsData.getIgnition());
                history.setVehicleStatus(gpsData.getVehicleStatus());
                history.setStatus(gpsData.getStatus());
                history.setTimeIntervals(gpsData.getTimeIntervals());
                history.setDistanceItervals(gpsData.getDistanceItervals());
                history.setGsmStrength(gpsData.getGsmStrength());

                // Decode additional data
                if (gpsData.getAdditionalData() != null && !gpsData.getAdditionalData().isEmpty()) {
                    try {
                        int additionalDataValue = Integer.parseInt(gpsData.getAdditionalData(), 2);
                        Map<String, Boolean> flags = decodeAdditionalData(additionalDataValue);
                        String decodedAdditionalData = flags.entrySet().stream()
                                .filter(Map.Entry::getValue)
                                .map(Map.Entry::getKey)
                                .collect(Collectors.joining(", "));
                        logger.info("\u001B[34m[INFO] Decoded Additional Data for {}: {}\u001B[0m",
                                deviceID, flags);
                        history.setAdditionalData(decodedAdditionalData);
                    } catch (NumberFormatException e) {
                        logger.warn("\u001B[33m[WARN] Could not parse additional data: {}\u001B[0m",
                                gpsData.getAdditionalData());
                    }
                }

                synchronized (historyBatch) {
                    if (historyBatch.isEmpty()) {
                        logger.info("\u001B[32m[SAVE] Saving single GPS record immediately for {}\u001B[0m", deviceID);
                        vehicleHistoryService.save(history);
                    } else {
                        historyBatch.add(history);
                        if (historyBatch.size() >= BATCH_SIZE) {
                            saveBatches();
                        }
                    }
                }

                // ✅ Save last known location
             // ✅ Save last known location (IMEI-first, fallback to DeviceID)
                VehicleLastLocation lastLocation = null;

                // 1️⃣ Try to find by IMEI if available
                if (imei != null && !imei.trim().isEmpty()) {
                    lastLocation = vehicleLastLocationRepository.findByImei(imei.trim()).orElse(null);
                    logger.info("🔍 Searched by IMEI: {}", imei);
                }

                // 2️⃣ If IMEI is missing or no record found, fallback to DeviceID
                if (lastLocation == null) {
                    lastLocation = vehicleLastLocationRepository.findByDeviceId(deviceID.trim()).orElse(null);
                    logger.info("🔍 Searched by DeviceID: {}", deviceID);
                }

                // 3️⃣ If still not found ➔ create new record
                if (lastLocation == null) {
                    lastLocation = new VehicleLastLocation();
                    logger.info("🆕 No existing record found ➔ creating new VehicleLastLocation for DeviceID: {}", deviceID);
                }

                // ✅ Always set Device ID
                lastLocation.setDeviceId(deviceID);

                // ✅ Set IMEI (if available)
                if (imei != null && !imei.trim().isEmpty()) {
                    lastLocation.setImei(imei.trim());
                    logger.info("✅ [IMEI SET] IMEI {} saved for Device ID: {}", imei, deviceID);
                } else {
                    logger.warn("⚠️ [WARN] IMEI is missing or empty for Device ID: {}", deviceID);
                }

                // ✅ Set all other live data fields
                lastLocation.setLatitude(latitude);
                lastLocation.setLongitude(longitude);
                lastLocation.setTimestamp(Timestamp.valueOf(gpsData.getTimestamp()));
                lastLocation.setStatus(gpsData.getStatus());
                lastLocation.setIgnition(gpsData.getIgnition());
                lastLocation.setCourse(gpsData.getCourse());
                lastLocation.setVehicleStatus(gpsData.getVehicleStatus());
                lastLocation.setSpeed(gpsData.getSpeed());

                // ✅ Save to DB
                vehicleLastLocationRepository.save(lastLocation);
                logger.info("\u001B[32m[GPS] Updated last known location for {}\u001B[0m", deviceID);

                	logger.info("\u001B[32m[GPS] Updated last known location for {}\u001B[0m", deviceID);

                

                logger.info("[WebSocket] Sending location update for device {}", deviceID);
                messagingTemplate.convertAndSend("/topic/location-updates",
                        new LocationUpdate(latitude, longitude, deviceID, gpsData.getTimestamp(),
                                gpsData.getSpeed(),
                                gpsData.getIgnition(),
                                gpsData.getCourse(),
                                gpsData.getVehicleStatus(),
                                gpsData.getAdditionalData(),
                                gpsData.getTimeIntervals()));

            } else {
                logger.error("\u001B[31m[ERROR] No vehicle found with deviceID {} and IMEI {} (cannot save GPS data)\u001B[0m",
                        deviceID, imei != null ? imei : "null");
            }

        } catch (Exception e) {
            logger.error("\u001B[31m[ERROR] Exception while processing GPS Data: {}\u001B[0m", gpsData, e);
        }
    }

    
    
    
    
    
    
    

    /**
     * Decodes Additional Data flags using bitwise operations.
     */
    private Map<String, Boolean> decodeAdditionalData(int additionalData) {
        Map<String, Boolean> flags = new HashMap<>();
        flags.put("Speed Crossed", (additionalData & 0b00000001) != 0);
        flags.put("Angle Change > 30°", (additionalData & 0b00000010) != 0);
        flags.put("Theft/Towing", (additionalData & 0b00000100) != 0);
        flags.put("Sharp Turning", (additionalData & 0b00001000) != 0);
        flags.put("Distance Change", (additionalData & 0b00010000) != 0);
        flags.put("Roaming", (additionalData & 0b00100000) != 0);
        flags.put("Harsh Acceleration", (additionalData & 0b01000000) != 0);
        flags.put("Harsh Breaking", (additionalData & 0b10000000) != 0);
        return flags;
    }

    @Scheduled(fixedRate = 2000)
    private void saveBatches() {
        synchronized (historyBatch) {
            if (!historyBatch.isEmpty()) {
                vehicleHistoryService.saveAll(new ArrayList<>(historyBatch));
                logger.info("\u001B[35m[Batch] Successfully saved {} records.\u001B[0m", historyBatch.size());
                historyBatch.clear();
            }
        }
    }

    @PreDestroy
    public void shutdownExecutor() {
        logger.info("\u001B[33m[SHUTDOWN] Shutting down executor service\u001B[0m");
        executorService.shutdownNow();
    }

    private double parseCoordinate(String coordinate) {
        return coordinate.isEmpty() ? 0.0 : Double.parseDouble(coordinate);
    }

    private String hexToAscii(String hexStr) {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < hexStr.length(); i += 2) {
            output.append((char) Integer.parseInt(hexStr.substring(i, i + 2), 16));
        }
        return output.toString();
    }

    private boolean isHexadecimal(String payload) {
        return payload.matches("\\p{XDigit}+");
    }
    
    // Method to manually fetch and broadcast vehicle locations through WebSocket
    public void fetchAndBroadcastAllLocations() {
        logger.info("\u001B[34m[WebSocket] Manual fetch request for all locations\u001B[0m");
        
        List<VehicleLastLocation> locations = vehicleLastLocationRepository.findAll();
        
        for (VehicleLastLocation location : locations) {
            LocationUpdate update = new LocationUpdate(
                location.getLatitude(),
                location.getLongitude(),
                location.getDeviceId(),
                location.getTimestamp().toString(),
                location.getSpeed(),
                location.getIgnition() ,
                location.getCourse() ,
                location.getVehicleStatus(),
                "",  // Additional Data
                ""   // Time Intervals
            );
            
            // Send to all subscribers
            messagingTemplate.convertAndSend("/topic/location-updates", update);
        }
        
        logger.info("\u001B[32m[WebSocket] Broadcast {} locations from database\u001B[0m", locations.size());
    }
    
    // Method to manually fetch and broadcast a specific vehicle location through WebSocket
    public void fetchAndBroadcastLocation(String deviceId) {
        logger.info("\u001B[34m[WebSocket] Manual location fetch request for device: {}\u001B[0m", deviceId);
        
        vehicleLastLocationRepository.findByDeviceId(deviceId).ifPresent(location -> {
            LocationUpdate update = new LocationUpdate(
                location.getLatitude(),
                location.getLongitude(),
                location.getDeviceId(),
                location.getTimestamp().toString(),
                location.getSpeed(),
                location.getIgnition() ,
                location.getCourse() ,
                location.getVehicleStatus(),
                "",  // Additional Data
                ""   // Time Intervals
            );
            
            // Send to all subscribers
            messagingTemplate.convertAndSend("/topic/location-updates", update);
            logger.info("\u001B[32m[WebSocket] Manually broadcast location for device: {}\u001B[0m", deviceId);
        });
    }
}
























/*
 * 
 * CODE COMMAD FOR 06/05/2025 IMIE DEVICE ID CHANGE 
 * 
 * 
 * 
 
@Service
public class MqttService {

    private static final Logger logger = LoggerFactory.getLogger(MqttService.class);

    @Autowired
    private MqttClient mqttClient;

    @Value("${mqtt.topics}")
    private String[] topics;

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

    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_CYAN = "\u001B[36m";

    private final ExecutorService executorService = Executors.newFixedThreadPool(10); 
    private final List<VehicleHistory> historyBatch = Collections.synchronizedList(new ArrayList<>());
    private static final int BATCH_SIZE = 100;

    @PostConstruct
    public void subscribe() {
        try {
            for (String topic : topics) {
                if (!topic.endsWith("#") && topic.contains("#")) {
                    logger.error("\u001B[31m[MQTT] Invalid topic: {}. Multi-level wildcard (#) can only appear at the end.\u001B[0m", topic);
                    continue;
                }

                mqttClient.subscribe(topic.trim(), (t, message) -> {
                    String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
                    logger.info("\u001B[34m[MQTT] Received Payload on {}: {}\u001B[0m", t, payload);

                    executorService.submit(() -> {
                        try {
                            String processedPayload = isHexadecimal(payload) ? hexToAscii(payload) : payload;
                            processPayload(processedPayload);
                        } catch (Exception e) {
                            logger.error("\u001B[31m[ERROR] Failed to process payload on topic {}: {}\u001B[0m", t, payload, e);
                        }
                    });
                });
                logger.info("\u001B[32m[MQTT] Subscribed to topic: {}\u001B[0m", topic);
            }

            // 🔥 Additional Subscription: Device Command Response Subscription
            mqttClient.subscribe("device/response/#", (topic, message) -> {
                String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
                logger.info("\u001B[36m[MQTT-RESPONSE] Received device response on {}: {}\u001B[0m", topic, payload);

                // Push the device response to WebSocket (for frontend to display)
                messagingTemplate.convertAndSend("/topic/device-responses", payload);
            });
            logger.info("\u001B[32m[MQTT] Subscribed to device/response/# topic for command responses\u001B[0m");

        } catch (MqttException e) {
            logger.error("\u001B[31m[MQTT] Error while subscribing: {}\u001B[0m", e.getMessage(), e);
        }
    }


    public void publish(String topic, String message) {
        try {
            MqttMessage mqttMessage = new MqttMessage(message.getBytes(StandardCharsets.UTF_8));
            mqttMessage.setQos(2);
            mqttClient.publish(topic, mqttMessage);
            logger.info("\u001B[36m[MQTT] Published message to {}: {}\u001B[0m", topic, message);
        } catch (MqttException e) {
            logger.error("\u001B[31m[MQTT] Error while publishing to {}: {}\u001B[0m", topic, e.getMessage(), e);
        }
    }

    /**
     * Process incoming GPS data payload (handles both single & batch).
     
    private void processPayload(String payload) {
        try {
            String cleanedPayload = payload.replaceAll("[^\\x00-\\x7F]", ""); 
            logger.info("\u001B[36m[INFO] Cleaned payload: {}\u001B[0m", cleanedPayload);

            // Attempt to parse as a JSON array
            if (cleanedPayload.trim().startsWith("[")) {
                // Handle as JSON Array
                List<GpsData> gpsDataList = objectMapper.readValue(cleanedPayload, new TypeReference<List<GpsData>>() {});
                logger.info("\u001B[32m[INFO] Processing batch of {} GPS records\u001B[0m", gpsDataList.size());
                gpsDataList.forEach(this::processGpsData);
            } else {
                // Handle single JSON object
                GpsData gpsData = objectMapper.readValue(cleanedPayload, GpsData.class);
                logger.info("\u001B[34m[INFO] Processing single GPS record for Device ID: {}\u001B[0m", gpsData.getDeviceID());
                processGpsData(gpsData);
            }
        } catch (JsonProcessingException e) {
            logger.error("\u001B[31m[ERROR] JSON Parsing failed: {}\u001B[0m", payload, e);
        } catch (Exception e) {
            logger.error("\u001B[31m[ERROR] Unexpected error while processing payload: {}\u001B[0m", payload, e);
        }
    }

    private void processGpsData(GpsData gpsData) {
        try {
            if (gpsData == null ||
                gpsData.getDeviceID() == null ||
                gpsData.getLatitude() == null ||
                gpsData.getLongitude() == null ||
                gpsData.getTimestamp() == null ||
                gpsData.getStatus() == null) {
                logger.error("\u001B[31m[ERROR] Invalid GPS data received: {}\u001B[0m", gpsData);
                return;
            }

            vehicleService.getVehicleByDeviceID(gpsData.getDeviceID()).ifPresentOrElse(vehicle -> {
                double latitude = Double.parseDouble(gpsData.getLatitude());
                double longitude = Double.parseDouble(gpsData.getLongitude());

                VehicleHistory history = new VehicleHistory();
                history.setVehicle(vehicle);
                history.setTimestamp(Timestamp.valueOf(gpsData.getTimestamp()));
                history.setLatitude(latitude);
                history.setLongitude(longitude);
                history.setSpeed(Double.valueOf(gpsData.getSpeed()));
                history.setCourse(gpsData.getCourse());
                history.setSequenceNumber(gpsData.getSequenceNumber());
                history.setIgnition(gpsData.getIgnition());
                history.setVehicleStatus(gpsData.getVehicleStatus());
                history.setStatus(gpsData.getStatus());
                
                history.setTimeIntervals(gpsData.getTimeIntervals());
                history.setDistanceItervals(gpsData.getDistanceItervals());
                history.setGsmStrength(gpsData.getGsmStrength());

                // Decode additionalData and store as a human-readable string
                if (gpsData.getAdditionalData() != null && !gpsData.getAdditionalData().isEmpty()) {
                    try {
                        int additionalDataValue = Integer.parseInt(gpsData.getAdditionalData(), 2);
                        Map<String, Boolean> flags = decodeAdditionalData(additionalDataValue);
                        String decodedAdditionalData = flags.entrySet().stream()
                            .filter(Map.Entry::getValue) // Include only true flags
                            .map(Map.Entry::getKey) // Get the key (description)
                            .collect(Collectors.joining(", ")); // Join with commas
                        logger.info("\u001B[34m[INFO] Decoded Additional Data for {}: {}\u001B[0m", gpsData.getDeviceID(), flags);
                        history.setAdditionalData(decodedAdditionalData); // Store decoded string
                    } catch (NumberFormatException e) {
                        logger.warn("\u001B[33m[WARN] Could not parse additional data: {}\u001B[0m", gpsData.getAdditionalData());
                    }
                }

                // Store single record immediately, batch otherwise
                synchronized (historyBatch) {
                    if (historyBatch.isEmpty()) {
                        logger.info("\u001B[32m[SAVE] Saving single GPS record immediately for {}\u001B[0m", gpsData.getDeviceID());
                        vehicleHistoryService.save(history);
                    } else {
                        historyBatch.add(history);
                        if (historyBatch.size() >= BATCH_SIZE) {
                            saveBatches();
                        }
                    }
                }

                // Update last known location
                VehicleLastLocation lastLocation = vehicleLastLocationRepository.findByDeviceId(gpsData.getDeviceID())
                    .orElse(new VehicleLastLocation());

                lastLocation.setDeviceId(gpsData.getDeviceID());
                lastLocation.setLatitude(latitude);
                lastLocation.setLongitude(longitude);
                lastLocation.setTimestamp(Timestamp.valueOf(gpsData.getTimestamp()));
                lastLocation.setStatus(gpsData.getStatus());
                lastLocation.setIgnition(gpsData.getIgnition());
                lastLocation.setCourse(gpsData.getCourse());
                lastLocation.setVehicleStatus(gpsData.getVehicleStatus());
                lastLocation.setSpeed(gpsData.getSpeed());

                vehicleLastLocationRepository.save(lastLocation);
                logger.info("\u001B[32m[GPS] Updated last known location for {}\u001B[0m", gpsData.getDeviceID());
                
                logger.info("[WebSocket] Sending location update for device {}", gpsData.getDeviceID());
                // Send update to frontend via WebSocket
                messagingTemplate.convertAndSend("/topic/location-updates",
                        new LocationUpdate(latitude, longitude, gpsData.getDeviceID(), gpsData.getTimestamp(),
                                gpsData.getSpeed(), 
                                gpsData.getIgnition() , 
                                gpsData.getCourse(), 
                                gpsData.getVehicleStatus(),
                                gpsData.getAdditionalData(),
                                gpsData.getTimeIntervals()));

            }, () -> logger.error("\u001B[31m[ERROR] Vehicle with deviceID {} not found in database.\u001B[0m", gpsData.getDeviceID()));

        } catch (Exception e) {
            logger.error("\u001B[31m[ERROR] Failed to process GPS Data: {}\u001B[0m", gpsData, e);
        }
    }

    /**
     * Decodes Additional Data flags using bitwise operations.
    
    private Map<String, Boolean> decodeAdditionalData(int additionalData) {
        Map<String, Boolean> flags = new HashMap<>();
        flags.put("Speed Crossed", (additionalData & 0b00000001) != 0);
        flags.put("Angle Change > 30°", (additionalData & 0b00000010) != 0);
        flags.put("Theft/Towing", (additionalData & 0b00000100) != 0);
        flags.put("Sharp Turning", (additionalData & 0b00001000) != 0);
        flags.put("Distance Change", (additionalData & 0b00010000) != 0);
        flags.put("Roaming", (additionalData & 0b00100000) != 0);
        flags.put("Harsh Acceleration", (additionalData & 0b01000000) != 0);
        flags.put("Harsh Breaking", (additionalData & 0b10000000) != 0);
        return flags;
    }

    @Scheduled(fixedRate = 2000)
    private void saveBatches() {
        synchronized (historyBatch) {
            if (!historyBatch.isEmpty()) {
                vehicleHistoryService.saveAll(new ArrayList<>(historyBatch));
                logger.info("\u001B[35m[Batch] Successfully saved {} records.\u001B[0m", historyBatch.size());
                historyBatch.clear();
            }
        }
    }

    @PreDestroy
    public void shutdownExecutor() {
        logger.info("\u001B[33m[SHUTDOWN] Shutting down executor service\u001B[0m");
        executorService.shutdownNow();
    }

    private double parseCoordinate(String coordinate) {
        return coordinate.isEmpty() ? 0.0 : Double.parseDouble(coordinate);
    }

    private String hexToAscii(String hexStr) {
        StringBuilder output = new StringBuilder();
        for (int i = 0; i < hexStr.length(); i += 2) {
            output.append((char) Integer.parseInt(hexStr.substring(i, i + 2), 16));
        }
        return output.toString();
    }

    private boolean isHexadecimal(String payload) {
        return payload.matches("\\p{XDigit}+");
    }
    
    // Method to manually fetch and broadcast vehicle locations through WebSocket
    public void fetchAndBroadcastAllLocations() {
        logger.info("\u001B[34m[WebSocket] Manual fetch request for all locations\u001B[0m");
        
        List<VehicleLastLocation> locations = vehicleLastLocationRepository.findAll();
        
        for (VehicleLastLocation location : locations) {
            LocationUpdate update = new LocationUpdate(
                location.getLatitude(),
                location.getLongitude(),
                location.getDeviceId(),
                location.getTimestamp().toString(),
                location.getSpeed(),
                location.getIgnition() ,
                location.getCourse() ,
                location.getVehicleStatus(),
                "",  // Additional Data
                ""   // Time Intervals
            );
            
            // Send to all subscribers
            messagingTemplate.convertAndSend("/topic/location-updates", update);
        }
        
        logger.info("\u001B[32m[WebSocket] Broadcast {} locations from database\u001B[0m", locations.size());
    }
    
    // Method to manually fetch and broadcast a specific vehicle location through WebSocket
    public void fetchAndBroadcastLocation(String deviceId) {
        logger.info("\u001B[34m[WebSocket] Manual location fetch request for device: {}\u001B[0m", deviceId);
        
        vehicleLastLocationRepository.findByDeviceId(deviceId).ifPresent(location -> {
            LocationUpdate update = new LocationUpdate(
                location.getLatitude(),
                location.getLongitude(),
                location.getDeviceId(),
                location.getTimestamp().toString(),
                location.getSpeed(),
                location.getIgnition() ,
                location.getCourse() ,
                location.getVehicleStatus(),
                "",  // Additional Data
                ""   // Time Intervals
            );
            
            // Send to all subscribers
            messagingTemplate.convertAndSend("/topic/location-updates", update);
            logger.info("\u001B[32m[WebSocket] Manually broadcast location for device: {}\u001B[0m", deviceId);
        });
    }
}

*/