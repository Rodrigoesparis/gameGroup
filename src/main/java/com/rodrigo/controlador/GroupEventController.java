package com.rodrigo.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class GroupEventController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void notifyGroupUpdate(Integer groupId, String event) {
        messagingTemplate.convertAndSend("/topic/groups", 
            new GroupEvent(groupId, event));
    }

    public static class GroupEvent {
        public Integer groupId;
        public String event; // "CREATED", "MEMBER_JOINED", "MEMBER_LEFT"

        public GroupEvent(Integer groupId, String event) {
            this.groupId = groupId;
            this.event = event;
        }
    }
}
