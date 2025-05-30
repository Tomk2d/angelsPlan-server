package org.tomkidWorld.angelsPlan.domain.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class WebSocketTestController {

    @MessageMapping("/send")
    @SendTo("/topic/messages")
    public String handleMessage(String message) {
        System.out.println("Received message: " + message);
        return "Server received: " + message;
    }
} 