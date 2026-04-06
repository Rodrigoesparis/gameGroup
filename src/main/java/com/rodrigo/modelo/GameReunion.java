package com.rodrigo.modelo;

import jakarta.persistence.*;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "game_groups")
public class GameReunion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idGroup;

    // Nombre del grupo (ej: "Los del barrio", "Tarde de Cluedo")
    private String name;

    // Juego al que van a jugar (ej: "Cluedo", "Uno", "Among Us")
    private String game;

    // Modo de juego: COMPETITIVO, CASUAL, PERSONALIZADO
    private String mode;

    // Privacidad del grupo
    @Enumerated(EnumType.STRING)
    private Privacy privacy;

    // Contraseña (solo si privacy = PRIVADO_PASSWORD)
    private String password;

    // Máximo de jugadores permitidos
    private Integer maxPlayers;

    // Quién creó el grupo originalmente
    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;

    // Participantes del grupo
    @OneToMany(mappedBy = "group")
    @JsonIgnore
    private List<Participant> participant;

    // Solicitudes de entrada pendientes
    @OneToMany(mappedBy = "group")
    @JsonIgnore
    private List<Request> requests;

    public Integer getIdGroup() { return idGroup; }
    public void setIdGroup(Integer idGroup) { this.idGroup = idGroup; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getGame() { return game; }
    public void setGame(String game) { this.game = game; }

    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }

    public Privacy getPrivacy() { return privacy; }
    public void setPrivacy(Privacy privacy) { this.privacy = privacy; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Integer getMaxPlayers() { return maxPlayers; }
    public void setMaxPlayers(Integer maxPlayers) { this.maxPlayers = maxPlayers; }

    public User getCreator() { return creator; }
    public void setCreator(User creator) { this.creator = creator; }

    public List<Participant> getParticipant() { return participant; }
    public void setParticipant(List<Participant> participant) { this.participant = participant; }

    public List<Request> getRequests() { return requests; }
    public void setRequests(List<Request> requests) { this.requests = requests; }
}