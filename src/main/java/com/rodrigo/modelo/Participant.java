package com.rodrigo.modelo;

import jakarta.persistence.*;

@Entity
@Table(name = "participants")
@IdClass(ParticipantId.class) // para clave compuesta
public class Participant {

 @Id
 @ManyToOne
 @JoinColumn(name = "user_id")
 private User user;

 @Id
 @ManyToOne
 @JoinColumn(name = "group_id")
 private GameReunion group;

 @Enumerated(EnumType.STRING)
 private Role role = Role.MIEMBRO;

public User getUser() {
	return user;
}

public void setUser(User user) {
	this.user = user;
}

public GameReunion getGroup() {
	return group;
}

public void setGroup(GameReunion group) {
	this.group = group;
}

public Role getRole() {
	return role;
}

public void setRole(Role role) {
	this.role = role;
}
}
