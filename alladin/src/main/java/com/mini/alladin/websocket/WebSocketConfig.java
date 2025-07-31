package com.mini.alladin.websocket;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

/**
 * WebSocketConfig is a configuration class for WebSocket messaging in Spring Boot.
 * It enables WebSocket message handling and configuration for message brokers.
 * Activates STOMP over WebSocket. STOMP is a protocol that sits on top of WebSocket.
 * It allows for the use of message brokers to route messages to the correct clients.
 *
 */

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {


    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry){
        registry.addEndpoint("/ws") // URL to connect from frontend
                .setAllowedOriginPatterns("http://localhost:8080")
                .withSockJS(); // Use SockJS for fallback support
    }


    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry){
        registry.enableSimpleBroker("/topic"); // /topic/** -> Where backend pushes updates (where frontend subscribes to)
        registry.setApplicationDestinationPrefixes("/app");  // /app/** where frontend sends messages
    }
}
