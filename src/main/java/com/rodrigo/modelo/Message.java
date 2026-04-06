package com.rodrigo.modelo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private GameReunion group;

    private String sender;
    private String text;
    private LocalDateTime timestamp;

    public Message() {}

    public Message(GameReunion group, String sender, String text) {
        this.group = group;
        this.sender = sender;
        this.text = text;
        this.timestamp = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public GameReunion getGroup() { return group; }
    public String getSender() { return sender; }
    public String getText() { return text; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setGroup(GameReunion group) { this.group = group; }
    public void setSender(String sender) { this.sender = sender; }
    public void setText(String text) { this.text = text; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}