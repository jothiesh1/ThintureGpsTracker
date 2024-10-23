package com.GpsTracker.Thinture.config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.ServerSocket;

import java.io.IOException;


@Configuration
public class TcpServerConfig {

    private static final Logger logger = LoggerFactory.getLogger(TcpServerConfig.class);

    @Value("${tcp.server.port}")
    private int tcpPort;  // This should match the property name in your application.properties

    @Value("${tcp.server.ip}")
    private String tcpIp;

    @Bean
    public ServerSocket serverSocket() throws IOException {
        logger.info("Creating TCP Server socket on IP: {} and port: {}", tcpIp, tcpPort);
        ServerSocket serverSocket = new ServerSocket(tcpPort);

        // Handle graceful shutdown of the TCP Server
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                if (serverSocket != null && !serverSocket.isClosed()) {
                    logger.info("Closing the TCP Server socket on shutdown.");
                    serverSocket.close();
                }
            } catch (IOException e) {
                logger.error("Error closing TCP Server socket", e);
            }
        }));
        
        return serverSocket;
    }
}
