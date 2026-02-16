package com.logistics.driver.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(@org.springframework.lang.NonNull MessageBrokerRegistry config) {
        // Enable a simple in-memory message broker for subscriptions
        config.enableSimpleBroker("/topic", "/queue");

        // Prefix for messages from clients
        config.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(@org.springframework.lang.NonNull StompEndpointRegistry registry) {
        // WebSocket endpoint for driver location updates
        registry.addEndpoint("/ws/driver-location")
                .setAllowedOriginPatterns("*")
                .withSockJS();

        // WebSocket endpoint for general driver updates
        registry.addEndpoint("/ws/driver")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }
}
