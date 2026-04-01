package com.rodrigo.controlador;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    @MessageMapping("/chat/{groupId}")
    @SendTo("/topic/chat/{groupId}")
    public ChatMessage sendMessage(@DestinationVariable Integer groupId, ChatMessage message) {
        System.out.println("Mensaje recibido en grupo " + groupId + ": " + message.text);
        return message;
    }

    public static class ChatMessage {
        public String sender;
        public String text;
        public String time;

        public ChatMessage() {}

        public ChatMessage(String sender, String text, String time) {
            this.sender = sender;
            this.text = text;
            this.time = time;
        }
    }
}