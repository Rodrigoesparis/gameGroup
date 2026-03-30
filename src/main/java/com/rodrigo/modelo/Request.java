package com.rodrigo.modelo;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
public class Request {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Integer id;

 @ManyToOne
 @JoinColumn(name = "user_id")
 private User user;

 @ManyToOne
 @JoinColumn(name = "group_id")
 private GameGroup group;

 @Enumerated(EnumType.STRING)
 private RequestStatus status = RequestStatus.PENDIENTE;

 private LocalDateTime createdAt = LocalDateTime.now();

 @ManyToOne
 @JoinColumn(name = "decided_by")
 private User decidedBy; // líder que aceptó/rechazó

public Integer getId() {
	return id;
}

public void setId(Integer id) {
	this.id = id;
}

public User getUser() {
	return user;
}

public void setUser(User user) {
	this.user = user;
}

public GameGroup getGroup() {
	return group;
}

public void setGroup(GameGroup group) {
	this.group = group;
}

public RequestStatus getStatus() {
	return status;
}

public void setStatus(RequestStatus status) {
	this.status = status;
}

public LocalDateTime getCreatedAt() {
	return createdAt;
}

public void setCreatedAt(LocalDateTime createdAt) {
	this.createdAt = createdAt;
}

public User getDecidedBy() {
	return decidedBy;
}

public void setDecidedBy(User decidedBy) {
	this.decidedBy = decidedBy;
}
}
