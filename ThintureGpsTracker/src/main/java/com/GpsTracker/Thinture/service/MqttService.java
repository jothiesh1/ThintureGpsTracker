package com.GpsTracker.Thinture.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.GpsTracker.Thinture.dto.LocationUpdate;
import com.GpsTracker.Thinture.model.CoordinatesCache;
import com.GpsTracker.Thinture.model.GpsData;
import com.GpsTracker.Thinture.model.Vehicle;
import com.GpsTracker.Thinture.model.VehicleHistory;
import com.GpsTracker.Thinture.model.VehicleLastLocation;
import com.GpsTracker.Thinture.repository.CoordinatesCacheRepository;
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
import org.springframework.dao.DataIntegrityViolationException;
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
    // Add this autowired service to your existing MqttService
    @Autowired
    private GeocodingService geocodingService;
    @Autowired
    private VehicleLastLocationRepository vehicleLastLocationRepository;
  
    
    
    @Autowired
    private CoordinatesCacheRepository coordinatesCacheRepository;
    
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
            logger.info("{}[INFO] Cleaned payload: {}{}", ANSI_CYAN, cleanedPayload, ANSI_RESET);

            if (cleanedPayload.trim().startsWith("[")) {
                // ✅ Proper JSON array
                List<GpsData> gpsDataList = objectMapper.readValue(cleanedPayload, new TypeReference<List<GpsData>>() {});
                logger.info("{}[INFO] Processing batch of {} GPS records{}", ANSI_GREEN, gpsDataList.size(), ANSI_RESET);
                gpsDataList.forEach(this::processGpsData);

            } else if (cleanedPayload.trim().startsWith("{")) {
                // ✅ Handle concatenated objects
                List<String> objects = splitConcatenatedJsonObjects(cleanedPayload);
                logger.info("{}[INFO] Detected {} concatenated JSON objects{}", ANSI_GREEN, objects.size(), ANSI_RESET);

                for (String obj : objects) {
                    try {
                        GpsData gpsData = objectMapper.readValue(obj, GpsData.class);
                        processGpsData(gpsData);
                    } catch (JsonProcessingException e) {
                        logger.error("{}[ERROR] Failed to parse JSON object: {}{}", ANSI_RED, obj, ANSI_RESET, e);
                    }
                }

            } else {
                logger.error("{}[ERROR] Unknown payload format: {}{}", ANSI_RED, cleanedPayload, ANSI_RESET);
            }

        } catch (Exception e) {
            logger.error("{}[ERROR] Unexpected error while processing payload: {}{}", ANSI_RED, payload, ANSI_RESET, e);
        }
    }
    private List<String> splitConcatenatedJsonObjects(String input) {
        List<String> jsonObjects = new ArrayList<>();
        int braceCount = 0;
        int start = 0;
        for (int i = 0; i < input.length(); i++) {
            char c = input.charAt(i);
            if (c == '{') {
                if (braceCount == 0) {
                    start = i;
                }
                braceCount++;
            } else if (c == '}') {
                braceCount--;
                if (braceCount == 0) {
                    String jsonObject = input.substring(start, i + 1);
                    jsonObjects.add(jsonObject);
                }
            }
        }
        return jsonObjects;
    }

    
    /**
     * UPDATED processGpsData - Auto-save unique coordinates to CoordinatesCache
     * (WITHOUT calling address API - just store lat/lng pairs)
     */
    private void processGpsData(GpsData gpsData) {
        try {
            // ✅ Validate GPS data (same as before)
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
            String status = gpsData.getStatus().trim();

            logger.info("[INFO] Processing GPS Data: deviceID={}, IMEI={}, STATUS={}", deviceID, imei, status);

            // ✅ Find vehicle by IMEI (same as before)
            Optional<Vehicle> vehicleOpt = vehicleService.getVehicleByImei(imei);
            if (vehicleOpt.isEmpty()) {
                logger.error("[ERROR] No vehicle found with IMEI={} (cannot save GPS data)", imei);
                return;
            }

            Vehicle vehicle = vehicleOpt.get();
            String dbDeviceID = vehicle.getDeviceID() != null ? vehicle.getDeviceID().trim() : "";

            // ✅ Device ID validation (same as before)
            if (dbDeviceID.isEmpty()) {
                vehicle.setDeviceID(deviceID);
                vehicleService.save(vehicle);
                logger.info("🆕 First-time deviceID registered: {} for IMEI={}", deviceID, imei);
            } else {
                if (!deviceID.equalsIgnoreCase(dbDeviceID)) {
                    logger.warn("❌ DeviceID mismatch. Incoming={}, DB={}. Skipping save.", deviceID, dbDeviceID);
                    return;
                }
            }

            // ✅ Parse coordinates
            double latitude = Double.parseDouble(gpsData.getLatitude());
            double longitude = Double.parseDouble(gpsData.getLongitude());

            // 🆕 NEW: Auto-save unique coordinates to CoordinatesCache (NO API call yet)
            saveUniqueCoordinatesToCache(latitude, longitude, deviceID);

            // ✅ Create VehicleHistory record (same as before)
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

            // ✅ Process additional data (same as before)
            if (gpsData.getAdditionalData() != null && !gpsData.getAdditionalData().isEmpty()) {
                try {
                    int additionalDataValue = Integer.parseInt(gpsData.getAdditionalData(), 2);
                    
                    if (additionalDataValue > 1023) {
                        logger.warn("{}[WARN] Additional data value {} exceeds 10-bit range (max 1023) for device {}{}",
                                ANSI_YELLOW, additionalDataValue, deviceID, ANSI_RESET);
                    }
                    
                    Map<String, Boolean> flags = decodeAdditionalData(additionalDataValue);
                    String decoded = flags.entrySet().stream()
                            .filter(Map.Entry::getValue)
                            .map(Map.Entry::getKey)
                            .collect(Collectors.joining(", "));
                    
                    history.setAdditionalData(decoded.isEmpty() ? "No Active Alerts" : decoded);
                    
                    logger.info("{}[INFO] Decoded Additional Data for {}: {}{}", 
                            ANSI_GREEN, deviceID, decoded.isEmpty() ? "No Active Alerts" : decoded, ANSI_RESET);
                    
                } catch (NumberFormatException e) {
                    logger.error("{}[ERROR] Could not parse additional data as binary: {} for device {}{}",
                            ANSI_RED, gpsData.getAdditionalData(), deviceID, ANSI_RESET, e);
                    history.setAdditionalData("Parse Error: " + gpsData.getAdditionalData());
                }
            } else {
                history.setAdditionalData("No Additional Data");
            }

            // ✅ Save vehicle history (same as before)
            synchronized (historyBatch) {
                if (historyBatch.isEmpty()) {
                    logger.info("{}[SAVE] 💾 Saving GPS record for {} | Lat: {}, Lng: {}{}",
                            ANSI_GREEN, deviceID, latitude, longitude, ANSI_RESET);
                    vehicleHistoryService.save(history);
                } else {
                    historyBatch.add(history);
                    logger.info("{}[BATCH] 📦 Added to batch ({}/{}) for {} | Lat: {}, Lng: {}{}",
                            ANSI_BLUE, historyBatch.size(), BATCH_SIZE, deviceID, latitude, longitude, ANSI_RESET);
                    
                    if (historyBatch.size() >= BATCH_SIZE) {
                        logger.info("{}[BATCH] 💾 Saving batch of {} GPS records{}",
                                ANSI_GREEN, historyBatch.size(), ANSI_RESET);
                        saveBatches();
                    }
                }
            }

            // ✅ Save/update last location (same as before)
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
            lastLocation.setTimeIntervals(gpsData.getTimeIntervals());
            lastLocation.setGsmStrength(gpsData.getGsmStrength());

            vehicleLastLocationRepository.save(lastLocation);
            logger.info("{}[GPS] 📍 Updated last location for {} | Lat: {}, Lng: {}{}",
                    ANSI_CYAN, deviceID, latitude, longitude, ANSI_RESET);

            // ✅ WebSocket logic (same as before)
            if (!"N1".equalsIgnoreCase(status)) {
                logger.info("{}🚫 [WebSocket] Blocking WebSocket update for device {} with status {} (Only N1 allowed){}",
                        ANSI_YELLOW, deviceID, status, ANSI_RESET);
                return;
            }

            logger.info("{}✅ [WebSocket] Sending location update for device {} with status {}{}",
                    ANSI_GREEN, deviceID, status, ANSI_RESET);

            LocationUpdate update = new LocationUpdate(
                    latitude,
                    longitude,
                    deviceID,
                    gpsData.getTimestamp(),
                    gpsData.getSpeed(),
                    gpsData.getIgnition(),
                    gpsData.getCourse(),
                    gpsData.getVehicleStatus(),
                    gpsData.getAdditionalData(),
                    gpsData.getGsmStrength(),
                    gpsData.getTimeIntervals()
            );

            // ✅ Send WebSocket updates (same as before)
            if (vehicle.getDealer_id() != null) {
                String topic = "/topic/location-updates/dealer/" + vehicle.getDealer_id();
                messagingTemplate.convertAndSend(topic, update);
                logger.info("{}📡 [WS] Sent to => DEALER (ID={}) | deviceID={} | Lat: {}, Lng: {}{}",
                        ANSI_CYAN, vehicle.getDealer_id(), deviceID, latitude, longitude, ANSI_RESET);
            }

            if (vehicle.getAdmin_id() != null) {
                String topic = "/topic/location-updates/admin/" + vehicle.getAdmin_id();
                messagingTemplate.convertAndSend(topic, update);
                logger.info("{}📡 [WS] Sent to => ADMIN (ID={}) | deviceID={} | Lat: {}, Lng: {}{}",
                        ANSI_CYAN, vehicle.getAdmin_id(), deviceID, latitude, longitude, ANSI_RESET);
            }

            if (vehicle.getClient_id() != null) {
                String topic = "/topic/location-updates/client/" + vehicle.getClient_id();
                messagingTemplate.convertAndSend(topic, update);
                logger.info("{}📡 [WS] Sent to => CLIENT (ID={}) | deviceID={} | Lat: {}, Lng: {}{}",
                        ANSI_CYAN, vehicle.getClient_id(), deviceID, latitude, longitude, ANSI_RESET);
            }

            if (vehicle.getUser_id() != null) {
                String topic = "/topic/location-updates/user/" + vehicle.getUser_id();
                messagingTemplate.convertAndSend(topic, update);
                logger.info("{}📡 [WS] Sent to => USER (ID={}) | deviceID={} | Lat: {}, Lng: {}{}",
                        ANSI_CYAN, vehicle.getUser_id(), deviceID, latitude, longitude, ANSI_RESET);
            }
            
            if (vehicle.getSuperadmin_id() != null) {
                String topic = "/topic/location-updates/superadmin/" + vehicle.getSuperadmin_id();
                messagingTemplate.convertAndSend(topic, update);
                logger.info("{}📡 [WS] Sent to => SUPERADMIN (ID={}) | deviceID={} | Lat: {}, Lng: {}{}",
                        ANSI_CYAN, vehicle.getSuperadmin_id(), deviceID, latitude, longitude, ANSI_RESET);
            }

        } catch (Exception e) {
            logger.error("{}[ERROR] Exception while processing GPS Data: {}{}",
                    ANSI_RED, gpsData, ANSI_RESET, e);
        }
    }

    /**
     * 🆕 NEW METHOD: Save unique coordinates to CoordinatesCache (without address)
     * This prevents duplicate lat/lng pairs from being saved
     */
    private void saveUniqueCoordinatesToCache(double latitude, double longitude, String deviceID) {
        try {
            // Check if this coordinate pair already exists
            boolean exists = coordinatesCacheRepository.existsByLatitudeAndLongitude(latitude, longitude);
            
            if (exists) {
                logger.debug("{}📍 [COORD CACHE] Coordinates {},{} already exist for device {}{}",
                        ANSI_GREEN, latitude, longitude, deviceID, ANSI_RESET);
                return; // Skip saving duplicate coordinates
            }
            
            // Save new coordinate pair (without address - address will be null/empty)
            CoordinatesCache coordinateEntry = new CoordinatesCache();
            coordinateEntry.setLatitude(latitude);
            coordinateEntry.setLongitude(longitude);
            coordinateEntry.setAddress(""); // Empty address - will be filled when requested
            
            coordinatesCacheRepository.save(coordinateEntry);
            
            logger.info("{}🆕 [COORD CACHE] Saved new coordinates: {},{} from device {}{}",
                    ANSI_BLUE, latitude, longitude, deviceID, ANSI_RESET);
            
        } catch (DataIntegrityViolationException e) {
            // Handle duplicate key constraint gracefully (race condition)
            logger.debug("{}📍 [COORD CACHE] Duplicate coordinates {},{} detected (race condition) - device: {}{}",
                    ANSI_GREEN, latitude, longitude, deviceID, ANSI_RESET);
            
        } catch (Exception e) {
            logger.error("{}[COORD CACHE ERROR] Failed to save coordinates {},{} for device {}: {}{}",
                    ANSI_RED, latitude, longitude, deviceID, e.getMessage(), ANSI_RESET);
        }
    }
    
    
    
    
    
    /**
     * Decodes Additional Data flags using bitwise operations for 10-bit format.
     * 
     * BIT POSITIONS:
     * 0 - Over Speed
     * 1 - Angle Change > 30°
     * 2 - Theft/Towing
     * 3 - Sharp Turning
     * 4 - Distance Change
     * 5 - Roaming
     * 6 - Harsh Acceleration
     * 7 - Harsh Breaking
     * 8 - Box Open
     * 9 - Power Cut
     */
    private Map<String, Boolean> decodeAdditionalData(int additionalData) {
        Map<String, Boolean> flags = new LinkedHashMap<>(); // Using LinkedHashMap to maintain order
        
        // Original 8 bits (0-7)
        flags.put("Over Speed", (additionalData & 0b0000000001) != 0);           // Bit 0
        flags.put("Angle Change > 30°", (additionalData & 0b0000000010) != 0);   // Bit 1
        flags.put("Theft/Towing", (additionalData & 0b0000000100) != 0);         // Bit 2
        flags.put("Sharp Turning", (additionalData & 0b0000001000) != 0);        // Bit 3
        flags.put("Distance Change", (additionalData & 0b0000010000) != 0);      // Bit 4
        flags.put("Roaming", (additionalData & 0b0000100000) != 0);              // Bit 5
        flags.put("Harsh Acceleration", (additionalData & 0b0001000000) != 0);   // Bit 6
        flags.put("Harsh Breaking", (additionalData & 0b0010000000) != 0);       // Bit 7
        
        // New 2 bits (8-9)
        flags.put("Box Open", (additionalData & 0b0100000000) != 0);             // Bit 8
        flags.put("Power Cut", (additionalData & 0b1000000000) != 0);            // Bit 9
        
        return flags;
    }

    /**
     * Helper method to log detailed bit analysis for debugging
     */
    private void logBitAnalysis(String deviceID, String additionalDataString, int additionalDataValue, Map<String, Boolean> flags) {
        logger.info("{}[BIT ANALYSIS] Device: {} | Raw: {} | Decimal: {} | Binary: {}{}",
                ANSI_CYAN, deviceID, additionalDataString, additionalDataValue, 
                String.format("%10s", Integer.toBinaryString(additionalDataValue)).replace(' ', '0'), ANSI_RESET);
        
        // Log active flags only
        String activeFlags = flags.entrySet().stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .collect(Collectors.joining(", "));
        
        if (!activeFlags.isEmpty()) {
            logger.info("{}[ACTIVE ALERTS] Device: {} | Flags: {}{}",
                    ANSI_YELLOW, deviceID, activeFlags, ANSI_RESET);
        } else {
            logger.info("{}[NO ALERTS] Device: {} | All flags are inactive{}",
                    ANSI_GREEN, deviceID, ANSI_RESET);
        }
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
              //  location.getTimeIntervals(),
                
                
                "",  // Additional Data
                ""   // Time Intervals
, brokerUrl
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
                
            //    location.getTimeIntervals(),
                "",  // Additional Data
                ""   // Time Intervals
, deviceId
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