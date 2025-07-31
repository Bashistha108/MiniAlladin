package com.mini.alladin.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

/**
 * WebSocket Publisher
 *
 * This class is responsible for broadcasting real-time stock price updates
 * to all frontend clients subscribed to a specific topic.
 *
 * It uses Spring's SimpMessagingTemplate to send data to:
 *   /topic/stock-price/{symbol}
 *
 * Frontend clients listening to this topic will receive the latest price instantly.
 */
@Component
public class StockPriceWebSocketPublisher {

    private final SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    public StockPriceWebSocketPublisher(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    // Broadcasts updated price to WebSocket clients.
    public void sendLivePriceToFrondEndFromWebsocket(String symbol, double price){
        String topic = "/topic/stock-price/"+symbol; // destination
        simpMessagingTemplate.convertAndSend(topic, price); // send price to destination
    }

}















