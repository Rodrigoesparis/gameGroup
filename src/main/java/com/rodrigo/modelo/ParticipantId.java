package com.rodrigo.modelo;

import java.io.Serializable;
import java.util.Objects;

public class ParticipantId implements Serializable {

 private Integer user;
 private Integer group;

 public ParticipantId() {}

 public ParticipantId(Integer user, Integer group) {
     this.user = user;
     this.group = group;
 }

 @Override
 public boolean equals(Object o) {
     if(this == o) return true;
     if(!(o instanceof ParticipantId)) return false;
     ParticipantId that = (ParticipantId) o;
     return Objects.equals(user, that.user) && Objects.equals(group, that.group);
 }

 @Override
 public int hashCode() {
     return Objects.hash(user, group);
 }
}
