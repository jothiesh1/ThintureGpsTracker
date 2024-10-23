package com.GpsTracker.Thinture.controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.GpsTracker.Thinture.model.GpsData;
import com.GpsTracker.Thinture.service.TcpServerService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.OutputStream;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.OutputStream;
import java.net.Socket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.OutputStream;
import java.net.Socket;

@RestController
@RequestMapping("/api/tcp")
public class TcpController {

    private static final Logger logger = LoggerFactory.getLogger(TcpController.class);

    @Value("${tcp.server.ip}")
    private String tcpServerIp;

    @Value("${tcp.server.port}")
    private int tcpServerPort;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping("/send")
    public String sendData(@RequestBody String message) {
        try {
            GpsData gpsData;
            if (message.startsWith("{")) {
                gpsData = objectMapper.readValue(message, GpsData.class);
            } else {
                gpsData = new GpsData();
                gpsData.setAdditionalData(message);
            }
            logger.info("Received request to send GPS data to TCP server: {}", gpsData);

            String jsonPayload = objectMapper.writeValueAsString(gpsData);
            logger.info("Sending JSON payload to TCP server: {}", jsonPayload);

            sendTcpData(jsonPayload);
            logger.info("Sent GPS data successfully to TCP server: {}", gpsData);

            return "GPS data sent to TCP server: " + gpsData.toString();
        } catch (Exception e) {
            logger.error("Error while parsing GPS data: {}", e.getMessage(), e);
            return "Error while parsing GPS data: " + e.getMessage();
        }
    }

    private void sendTcpData(String message) throws Exception {
        try (Socket socket = new Socket(tcpServerIp, tcpServerPort)) {
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(message.getBytes());
            outputStream.flush();
            logger.info("Message sent to TCP server: {}", message);
        } catch (Exception e) {
            logger.error("Failed to send message to TCP server", e);
            throw e;
        }
    }
}
