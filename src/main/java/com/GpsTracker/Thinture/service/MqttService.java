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
import com.GpsTracker.Thinture.service.VehicleHistoryService;
import com.GpsTracker.Thinture.service.VehicleService;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Service
public class MqttService implements MqttCallbackExtended {

    private static final Logger logger = LoggerFactory.getLogger(MqttService.class);

    @Value("${mqtt.broker-url}")
    private String brokerUrl;

    @Value("${mqtt.client-id}")
    private String clientId;

    @Value("${mqtt.username}")
    private String username;

    @Value("${mqtt.password}")
    private String password;

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

    private MqttClient mqttClient;
    private final AtomicBoolean connectedToBroker = new AtomicBoolean(false);
    private final AtomicBoolean reconnecting = new AtomicBoolean(false);
    
    // Timestamp tracking for last received message
    private volatile long lastMessageTimestamp = 0;
    
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_CYAN = "\u001B[36m";

    private final ExecutorService executorService = Executors.newFixedThreadPool(10); 
    private final List<VehicleHistory> historyBatch = Collections.synchronizedList(new ArrayList<>());
    private static final int BATCH_SIZE = 100;
    private static final int MAX_RECONNECT_ATTEMPTS = 10;
    private static final int RECONNECT_DELAY_MS = 5000; // 5 seconds

    /**
     * Creates and configures the MQTT client with proper connection options
     */
    @Bean
    public MqttClient mqttClient() throws MqttException {
        try {
            // Use random client ID suffix to avoid conflicts on reconnection
            String randomizedClientId = clientId + "-" + UUID.randomUUID().toString().substring(0, 8);
            logger.info("{}[MQTT] Initializing MQTT client with ID: {}{}",
                    ANSI_GREEN, randomizedClientId, ANSI_RESET);
            
            // Use MemoryPersistence for reliability
            mqttClient = new MqttClient(brokerUrl, randomizedClientId, new MemoryPersistence());
            
            // Set callbacks for connection monitoring
            mqttClient.setCallback(this);
            
            MqttConnectOptions connectOptions = new MqttConnectOptions();
            connectOptions.setUserName(username);
            connectOptions.setPassword(password.toCharArray());
            connectOptions.setCleanSession(true);
            connectOptions.setAutomaticReconnect(true);
            connectOptions.setConnectionTimeout(30); // 30 seconds connection timeout
            connectOptions.setKeepAliveInterval(60); // 60 seconds keep alive
            connectOptions.setMaxInflight(100); // Maximum inflight messages
            
            logger.info("{}[MQTT] Connecting to broker: {}{}",
                    ANSI_YELLOW, brokerUrl, ANSI_RESET);
            
            mqttClient.connect(connectOptions);
            connectedToBroker.set(true);
            
            logger.info("{}[MQTT] Connected to broker successfully{}",
                    ANSI_GREEN, ANSI_RESET);
            
            return mqttClient;
        } catch (MqttException e) {
            logger.error("{}[MQTT] Failed to initialize MQTT client: {}{}",
                    ANSI_RED, e.getMessage(), ANSI_RESET, e);
            throw e;
        }
    }

    @PostConstruct
    public void init() {
        try {
            // Ensure we have an MQTT client
            if (mqttClient == null) {
                mqttClient = mqttClient();
            }
            
            // Subscribe to topics
            subscribe();
        } catch (MqttException e) {
            logger.error("{}[MQTT] Failed to initialize MQTT service: {}{}",
                    ANSI_RED, e.getMessage(), ANSI_RESET, e);
            // Schedule a reconnection attempt
            scheduleReconnection();
        }
    }

    /**
     * Subscribe to all configured topics
     */
    public void subscribe() {
        if (!mqttClient.isConnected()) {
            logger.error("{}[MQTT] Cannot subscribe - client not connected{}",
                    ANSI_RED, ANSI_RESET);
            return;
        }
        
        try {
            // Subscribe to configured topics
            for (String topic : topics) {
                if (!topic.endsWith("#") && topic.contains("#")) {
                    logger.error("{}[MQTT] Invalid topic: {}. Multi-level wildcard (#) can only appear at the end.{}",
                            ANSI_RED, topic, ANSI_RESET);
                    continue;
                }

                mqttClient.subscribe(topic.trim(), 2);
                logger.info("{}[MQTT] Subscribed to topic: {}{}",
                        ANSI_GREEN, topic, ANSI_RESET);
            }

            // Device Command Response Subscription
            mqttClient.subscribe("device/response/#", 2);
            logger.info("{}[MQTT] Subscribed to device/response/# topic for command responses{}",
                    ANSI_GREEN, ANSI_RESET);

        } catch (MqttException e) {
            logger.error("{}[MQTT] Error while subscribing: {}{}",
                    ANSI_RED, e.getMessage(), ANSI_RESET, e);
            // Schedule a reconnection attempt
            scheduleReconnection();
        }
    }

    /**
     * Reconnection logic for MQTT client
     */
    private synchronized void reconnect() {
        if (reconnecting.get()) {
            logger.info("{}[MQTT] Reconnection already in progress{}",
                    ANSI_YELLOW, ANSI_RESET);
            return;
        }
        
        reconnecting.set(true);
        int attemptCount = 0;
        
        while (!mqttClient.isConnected() && attemptCount < MAX_RECONNECT_ATTEMPTS) {
            attemptCount++;
            try {
                logger.info("{}[MQTT] Reconnection attempt {}/{}{}",
                        ANSI_YELLOW, attemptCount, MAX_RECONNECT_ATTEMPTS, ANSI_RESET);
                
                // Create new connection options
                MqttConnectOptions connectOptions = new MqttConnectOptions();
                connectOptions.setUserName(username);
                connectOptions.setPassword(password.toCharArray());
                connectOptions.setCleanSession(true);
                connectOptions.setAutomaticReconnect(true);
                connectOptions.setConnectionTimeout(30);
                connectOptions.setKeepAliveInterval(60);
                
                mqttClient.connect(connectOptions);
                
                if (mqttClient.isConnected()) {
                    connectedToBroker.set(true);
                    logger.info("{}[MQTT] Reconnected to broker successfully{}",
                            ANSI_GREEN, ANSI_RESET);
                    // Resubscribe to topics
                    subscribe();
                    break;
                }
            } catch (MqttException e) {
                logger.error("{}[MQTT] Failed reconnection attempt {}: {}{}",
                        ANSI_RED, attemptCount, e.getMessage(), ANSI_RESET, e);
                
                try {
                    Thread.sleep(RECONNECT_DELAY_MS);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    logger.error("{}[MQTT] Reconnection interrupted{}",
                            ANSI_RED, ANSI_RESET);
                    break;
                }
            }
        }
        
        if (!mqttClient.isConnected()) {
            connectedToBroker.set(false);
            logger.error("{}[MQTT] Failed to reconnect after {} attempts{}",
                    ANSI_RED, MAX_RECONNECT_ATTEMPTS, ANSI_RESET);
        }
        
        reconnecting.set(false);
    }

    /**
     * Schedule a reconnection attempt
     */
    private void scheduleReconnection() {
        new Thread(() -> {
            try {
                Thread.sleep(RECONNECT_DELAY_MS);
                reconnect();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    /**
     * Connection health check - runs every minute
     */
    @Scheduled(fixedRate = 60000)
    public void checkConnection() {
        if (!mqttClient.isConnected() && !reconnecting.get()) {
            logger.warn("{}[MQTT] Connection check: Not connected to broker{}",
                    ANSI_YELLOW, ANSI_RESET);
            scheduleReconnection();
        } else if (mqttClient.isConnected()) {
            logger.debug("{}[MQTT] Connection check: Connected to broker{}",
                    ANSI_GREEN, ANSI_RESET);
        }
        
        // Check last message timestamp to detect "silent failures"
        long currentTime = System.currentTimeMillis();
        long timeSinceLastMessage = currentTime - lastMessageTimestamp;
        
        // If we haven't received a message in 5 minutes and we're supposed to be connected
        if (lastMessageTimestamp > 0 && timeSinceLastMessage > 300000 && mqttClient.isConnected()) {
            logger.warn("{}[MQTT] No messages received in {} minutes. Forcing reconnection...{}",
                    ANSI_YELLOW, timeSinceLastMessage / 60000, ANSI_RESET);
            
            try {
                // Force disconnect and reconnect
                mqttClient.disconnect();
                scheduleReconnection();
            } catch (MqttException e) {
                logger.error("{}[MQTT] Error while forcing disconnect: {}{}",
                        ANSI_RED, e.getMessage(), ANSI_RESET, e);
            }
        }
    }

    /**
     * MQTT Callback: Called when connection to broker is lost
     */
    @Override
    public void connectionLost(Throwable cause) {
        connectedToBroker.set(false);
        logger.error("{}[MQTT] Connection to broker lost: {}{}",
                ANSI_RED, cause.getMessage(), ANSI_RESET, cause);
        scheduleReconnection();
    }

    /**
     * MQTT Callback: Called when a message has been delivered
     */
    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        try {
            logger.debug("{}[MQTT] Message delivery complete: {}{}",
                    ANSI_GREEN, Arrays.toString(token.getTopics()), ANSI_RESET);
        } catch (Exception e) {
            logger.error("{}[MQTT] Error in deliveryComplete callback{}",
                    ANSI_RED, ANSI_RESET, e);
        }
    }

    /**
     * MQTT Callback: Called when a message arrives from the server
     */
    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        // Update timestamp when we receive any message
        lastMessageTimestamp = System.currentTimeMillis();
        
        String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
        logger.info("{}[MQTT] Received Payload on {}: {}{}",
                ANSI_BLUE, topic, payload, ANSI_RESET);

        executorService.submit(() -> {
            try {
                String processedPayload = isHexadecimal(payload) ? hexToAscii(payload) : payload;
                processPayload(processedPayload);
            } catch (Exception e) {
                logger.error("{}[ERROR] Failed to process payload on topic {}: {}{}",
                        ANSI_RED, topic, payload, ANSI_RESET, e);
            }
        });
    }

    /**
     * MQTT Callback: Called when connection to broker is completed
     */
    @Override
    public void connectComplete(boolean reconnect, String serverURI) {
        connectedToBroker.set(true);
        if (reconnect) {
            logger.info("{}[MQTT] Reconnected to broker: {}{}",
                    ANSI_GREEN, serverURI, ANSI_RESET);
            // Resubscribe to topics after reconnection
            subscribe();
        } else {
            logger.info("{}[MQTT] Connected to broker: {}{}",
                    ANSI_GREEN, serverURI, ANSI_RESET);
        }
    }

    /**
     * Publish message to a topic
     */
    public void publish(String topic, String message) {
        if (!mqttClient.isConnected()) {
            logger.error("{}[MQTT] Cannot publish - client not connected{}",
                    ANSI_RED, ANSI_RESET);
            scheduleReconnection();
            return;
        }
        
        try {
            MqttMessage mqttMessage = new MqttMessage(message.getBytes(StandardCharsets.UTF_8));
            mqttMessage.setQos(2); // Using QoS 2 for exactly once delivery
            mqttMessage.setRetained(false);
            
            mqttClient.publish(topic, mqttMessage);
            logger.info("{}[MQTT] Published message to {}: {}{}",
                    ANSI_CYAN, topic, message, ANSI_RESET);
        } catch (MqttException e) {
            logger.error("{}[MQTT] Error while publishing to {}: {}{}",
                    ANSI_RED, topic, e.getMessage(), ANSI_RESET, e);
            if (e.getReasonCode() == MqttException.REASON_CODE_CONNECTION_LOST) {
                scheduleReconnection();
            }
        }
    }

    /**
     * Process incoming GPS data payload (handles both single & batch).
     */
    private void processPayload(String payload) {
        try {
            String cleanedPayload = payload.replaceAll("[^\\x00-\\x7F]", ""); 
            logger.info("{}[INFO] Cleaned payload: {}{}",
                    ANSI_CYAN, cleanedPayload, ANSI_RESET);

            // Attempt to parse as a JSON array
            if (cleanedPayload.trim().startsWith("[")) {
                // Handle as JSON Array
                List<GpsData> gpsDataList = objectMapper.readValue(cleanedPayload, new TypeReference<List<GpsData>>() {});
                logger.info("{}[INFO] Processing batch of {} GPS records{}",
                        ANSI_GREEN, gpsDataList.size(), ANSI_RESET);
                gpsDataList.forEach(this::processGpsData);
            } else {
                // Handle single JSON object
                GpsData gpsData = objectMapper.readValue(cleanedPayload, GpsData.class);
                logger.info("{}[INFO] Processing single GPS record for Device ID: {}{}",
                        ANSI_BLUE, gpsData.getDeviceID(), ANSI_RESET);
                processGpsData(gpsData);
            }
        } catch (JsonProcessingException e) {
            logger.error("{}[ERROR] JSON Parsing failed: {}{}",
                    ANSI_RED, payload, ANSI_RESET, e);
        } catch (Exception e) {
            logger.error("{}[ERROR] Unexpected error while processing payload: {}{}",
                    ANSI_RED, payload, ANSI_RESET, e);
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
                logger.error("[ERROR] Invalid GPS data received: {}", gpsData);
                return;
            }

            String deviceID = gpsData.getDeviceID().trim();
            String imei = gpsData.getImei() != null ? gpsData.getImei().trim() : "";

            logger.info("[INFO] Processing GPS Data: deviceID={}, IMEI={}", deviceID, imei);

            Optional<Vehicle> vehicleOpt = vehicleService.getVehicleByImei(imei);
            if (vehicleOpt.isEmpty()) {
                logger.error("[ERROR] No vehicle found with IMEI={} (cannot save GPS data)", imei);
                return;
            }

            Vehicle vehicle = vehicleOpt.get();
            String dbDeviceID = vehicle.getDeviceID() != null ? vehicle.getDeviceID().trim() : "";

            if (dbDeviceID.isEmpty()) {
                // ✅ First time: Save device ID into DB
                vehicle.setDeviceID(deviceID);
                vehicleService.save(vehicle);
                logger.info("🆕 First-time deviceID registered: {} for IMEI={}", deviceID, imei);
            } else {
                // ✅ Enforce strict match after first time
                if (!deviceID.equalsIgnoreCase(dbDeviceID)) {
                    logger.warn("❌ DeviceID mismatch. Incoming={}, DB={}. Skipping save.", deviceID, dbDeviceID);
                    return;
                }
            }

            // ✅ Save history (same logic as before)
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
            history.setImei(imei);

            if (gpsData.getAdditionalData() != null && !gpsData.getAdditionalData().isEmpty()) {
                try {
                    int additionalDataValue = Integer.parseInt(gpsData.getAdditionalData(), 2);
                    Map<String, Boolean> flags = decodeAdditionalData(additionalDataValue);
                    String decoded = flags.entrySet().stream()
                            .filter(Map.Entry::getValue)
                            .map(Map.Entry::getKey)
                            .collect(Collectors.joining(", "));
                    logger.info("[INFO] Decoded Additional Data for {}: {}", deviceID, flags);
                    history.setAdditionalData(decoded);
                } catch (NumberFormatException e) {
                    logger.warn("[WARN] Could not parse additional data: {}", gpsData.getAdditionalData());
                }
            }

            synchronized (historyBatch) {
                if (historyBatch.isEmpty()) {
                    logger.info("[SAVE] Saving single GPS record immediately for {}", deviceID);
                    vehicleHistoryService.save(history);
                } else {
                    historyBatch.add(history);
                    if (historyBatch.size() >= BATCH_SIZE) {
                        saveBatches();
                    }
                }
            }

            // ✅ Save/update last location
            VehicleLastLocation lastLocation = vehicleLastLocationRepository
                    .findByImei(imei).orElse(vehicleLastLocationRepository.findByDeviceId(deviceID).orElse(new VehicleLastLocation()));

            lastLocation.setDeviceId(deviceID);
            lastLocation.setImei(imei);
            lastLocation.setLatitude(latitude);
            lastLocation.setLongitude(longitude);
            lastLocation.setTimestamp(Timestamp.valueOf(gpsData.getTimestamp()));
            lastLocation.setStatus(gpsData.getStatus());
            lastLocation.setIgnition(gpsData.getIgnition());
            lastLocation.setCourse(gpsData.getCourse());
            lastLocation.setVehicleStatus(gpsData.getVehicleStatus());
            lastLocation.setSpeed(gpsData.getSpeed());

            vehicleLastLocationRepository.save(lastLocation);
            logger.info("[GPS] Updated last known location for {}", deviceID);

            messagingTemplate.convertAndSend("/topic/location-updates",
                    new LocationUpdate(latitude, longitude, deviceID, gpsData.getTimestamp(),
                            gpsData.getSpeed(), gpsData.getIgnition(),
                            gpsData.getCourse(), gpsData.getVehicleStatus(),
                            gpsData.getAdditionalData(), gpsData.getTimeIntervals()));
        } catch (Exception e) {
            logger.error("[ERROR] Exception while processing GPS Data: {}", gpsData, e);
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
                logger.info("{}[Batch] Successfully saved {} records.{}",
                        ANSI_CYAN, historyBatch.size(), ANSI_RESET);
                historyBatch.clear();
            }
        }
    }

    @PreDestroy
    public void cleanup() {
        logger.info("{}[SHUTDOWN] Cleaning up MQTT resources{}",
                ANSI_YELLOW, ANSI_RESET);
        
        try {
            // Process any remaining batch history entries
            saveBatches();
            
            // Shutdown executor service
            executorService.shutdownNow();
            
            // Disconnect MQTT client
            if (mqttClient != null && mqttClient.isConnected()) {
                mqttClient.disconnect();
                logger.info("{}[MQTT] Disconnected from broker{}",
                        ANSI_GREEN, ANSI_RESET);
            }
        } catch (MqttException e) {
            logger.error("{}[MQTT] Error during cleanup: {}{}",
                    ANSI_RED, e.getMessage(), ANSI_RESET, e);
        }
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
        logger.info("{}[WebSocket] Manual fetch request for all locations{}",
                ANSI_BLUE, ANSI_RESET);
        
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
        
        logger.info("{}[WebSocket] Broadcast {} locations from database{}",
                ANSI_GREEN, locations.size(), ANSI_RESET);
    }
    
    // Method to manually fetch and broadcast a specific vehicle location through WebSocket
    public void fetchAndBroadcastLocation(String deviceId) {
        logger.info("{}[WebSocket] Manual location fetch request for device: {}{}",
                ANSI_BLUE, deviceId, ANSI_RESET);
        
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
            logger.info("{}[WebSocket] Manually broadcast location for device: {}{}",
                    ANSI_GREEN, deviceId, ANSI_RESET);
        });
    }

    /**
     * Get connection status
     */
    public boolean isConnected() {
        return mqttClient != null && mqttClient.isConnected();
    }

    /**
     * Get the timestamp of the last received message
     */
    public long getLastMessageTimestamp() {
        return lastMessageTimestamp;
    }
    
    /**
     * Enhanced health check that verifies both connection status and message activity
     */
    @Scheduled(fixedRate = 120000) // Every 2 minutes
    public void checkMessageActivity() {
        long currentTime = System.currentTimeMillis();
        
        // If we've never received a message, this will be 0
        if (lastMessageTimestamp == 0) {
            logger.info("{}[HEALTH] No messages received yet{}",
                    ANSI_YELLOW, ANSI_RESET);
            return;
        }
        
        long timeElapsed = currentTime - lastMessageTimestamp;
        long minutesElapsed = timeElapsed / 60000;
        
        if (minutesElapsed > 5) { // No messages for 5 minutes
            logger.warn("{}[HEALTH] ⚠️ No messages received in {} minutes (last: {}){}",
                    ANSI_YELLOW, minutesElapsed, new Date(lastMessageTimestamp), ANSI_RESET);
            
            // If connected but not receiving messages, something might be wrong
            if (mqttClient.isConnected()) {
                logger.warn("{}[HEALTH] Client appears connected but no message activity - forcing reconnection{}",
                        ANSI_YELLOW, ANSI_RESET);
                try {
                    mqttClient.disconnect();
                    scheduleReconnection();
                } catch (MqttException e) {
                    logger.error("{}[HEALTH] Error forcing disconnect: {}{}",
                            ANSI_RED, e.getMessage(), ANSI_RESET);
                }
            }
        } else {
            logger.info("{}[HEALTH] ✅ Last message received {} minutes ago{}",
                    ANSI_GREEN, minutesElapsed, ANSI_RESET);
        }
    }
}