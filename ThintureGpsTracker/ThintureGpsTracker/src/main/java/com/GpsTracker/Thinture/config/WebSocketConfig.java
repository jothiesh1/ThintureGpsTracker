package com.GpsTracker.Thinture.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;


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
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // Configure the message broker with a thread pool scheduler
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(20); // Increase thread pool size
        taskScheduler.setThreadNamePrefix("WebSocketBrokerTask-");
        taskScheduler.initialize();

        // Configure the simple broker
        config.enableSimpleBroker("/topic")
              .setTaskScheduler(taskScheduler); // Set scheduler for message broker

        config.setApplicationDestinationPrefixes("/app"); // Prefix for messages bound for @MessageMapping
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Register the WebSocket endpoint and enable SockJS as a fallback
        registry.addEndpoint("/gs-guide-websocket")
                .setAllowedOriginPatterns("*") // Allow all origins (CORS)
                .withSockJS();
    }
}
