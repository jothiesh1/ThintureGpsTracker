package com.GpsTracker.Thinture.config;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.GpsTracker.Thinture.model.Vehicle;
import com.GpsTracker.Thinture.model.VehicleHistory;
 // Adjust to your actual package

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;

import org.eclipse.paho.client.mqttv3.MqttException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Configuration
public class MqttConfig {

    @Value("${mqtt.broker-url}")
    private String brokerUrl;

    @Value("${mqtt.client-id}")
    private String clientId;

    @Value("${mqtt.username}")
    private String username;

    @Value("${mqtt.password}")
    private String password;

    @Bean
    public MqttClient mqttClient() throws MqttException {
        // Use a specific directory for persistence
        String tmpDir = System.getProperty("java.io.tmpdir");
        MqttDefaultFilePersistence persistence = new MqttDefaultFilePersistence(tmpDir);

        // Create the MqttClient
        MqttClient mqttClient = new MqttClient(brokerUrl, clientId, persistence);

        // Set connection options
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        options.setUserName(username);
        options.setPassword(password.toCharArray());

        // Connect the client
        mqttClient.connect(options);

        return mqttClient;
    }
    
    @Bean
    public Map<String, Vehicle> vehicleCache() {
        return new ConcurrentHashMap<>();
    }
    
}









/*
@EnableScheduling
@Configuration
public class MqttConfig {

    @Value("${mqtt.broker-url}")
    private String brokerUrl;

    @Value("${mqtt.client-id}")
    private String clientId;

    @Value("${mqtt.username}")
    private String username;

    @Value("${mqtt.password}")
    private String password;

    @Bean
    public MqttClient mqttClient() throws MqttException {
        // Use a specific directory for persistence
        String tmpDir = System.getProperty("java.io.tmpdir");
        MqttDefaultFilePersistence persistence = new MqttDefaultFilePersistence(tmpDir);

        // Create the MqttClient
        MqttClient mqttClient = new MqttClient(brokerUrl, clientId, persistence);

        // Set connection options
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        options.setUserName(username);
        options.setPassword(password.toCharArray());

        // Connect the client
        mqttClient.connect(options);

        return mqttClient;
    }

    /**
     * ExecutorService with a larger thread pool than the default 10.
     * Adjust the size as needed for your environment and workloads.
     
    @Bean
    public ExecutorService executorService() {
        // A larger pool, e.g., 50
        return Executors.newFixedThreadPool(50);
    }

    /**
     * In-memory cache for Vehicles (optional).
     * Used to reduce DB lookups for vehicle data.
     
    @Bean
    public Map<String, VehicleHistory> vehicleCache() {
        return new ConcurrentHashMap<>();
    }
}

*/
