package com.GpsTracker.Thinture.config;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
}
