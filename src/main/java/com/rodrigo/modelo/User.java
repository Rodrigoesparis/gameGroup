package com.rodrigo.modelo;

import jakarta.persistence.*;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUser;
    //Nombre de la persona
    private String name;
    //Nombre de la cuenta
    @Column(unique = true)
    private String username;
    
    private String email;

    private String password;

    private Integer age;
    //Relacion con el grupo para creador
    @OneToMany(mappedBy = "creator")
    @JsonIgnore
    private List<GameGroup> createdGroups;
    //Relacion usuarios con participantes
    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Participant> participants;
    //Relacion de usuario con las peticiones
    @OneToMany(mappedBy = "user")
    @JsonIgnore
    private List<Request> requests;
    //Relacion de decision de la petición
    @OneToMany(mappedBy = "decidedBy")
    @JsonIgnore
    private List<Request> requestsDecided;
	public Integer getIdUser() {
		return idUser;
	}
	public void setIdUser(Integer idUser) {
		this.idUser = idUser;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Integer getAge() {
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	public List<GameGroup> getCreatedGroups() {
		return createdGroups;
	}
	public void setCreatedGroups(List<GameGroup> createdGroups) {
		this.createdGroups = createdGroups;
	}
	public List<Participant> getParticipants() {
		return participants;
	}
	public void setParticipants(List<Participant> participants) {
		this.participants = participants;
	}
	public List<Request> getRequests() {
		return requests;
	}
	public void setRequests(List<Request> requests) {
		this.requests = requests;
	}
	public List<Request> getRequestsDecided() {
		return requestsDecided;
	}
	public void setRequestsDecided(List<Request> requestsDecided) {
		this.requestsDecided = requestsDecided;
	}
}