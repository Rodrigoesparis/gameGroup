package com.rodrigo.controlador;

import com.rodrigo.modelo.GameReunion;
import com.rodrigo.modelo.Message;
import com.rodrigo.repositorio.GameReunionRepository;
import com.rodrigo.repositorio.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ChatController {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private GameReunionRepository groupRepository;

//ENDPOINT mensajes mandar recivir y guardar
    @MessageMapping("/chat/{groupId}")
    @SendTo("/topic/chat/{groupId}")
    public ChatMessage sendMessage(
            @DestinationVariable Integer groupId,
            ChatMessage message) {

        // Guardar en BD
        GameReunion group = groupRepository.findById(groupId).orElse(null);
        if (group != null) {
            Message msg = new Message(group, message.sender, message.text);
            messageRepository.save(msg);
        }

        System.out.println("Mensaje guardado en grupo " + groupId + ": " + message.text);
        return message;
    }

    //ENDPOINT para cargar el historial y aparezcan siempre mensajes antiguos
    @GetMapping("/chat/{groupId}/history")
    @org.springframework.web.bind.annotation.ResponseBody
    public ResponseEntity<List<ChatMessage>> getHistory(
            @PathVariable Integer groupId) {

        final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm");
        List<ChatMessage> history = messageRepository
                .findByGroupIdGroupOrderByTimestampAsc(groupId)
                .stream()
                .map(m -> new ChatMessage(
                        m.getSender(),
                        m.getText(),
                        m.getTimestamp().format(fmt)))
                .collect(Collectors.toList());

        return ResponseEntity.ok(history);
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