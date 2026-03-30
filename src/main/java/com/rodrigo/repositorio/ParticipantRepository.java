package com.rodrigo.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import com.rodrigo.modelo.Participant;
import com.rodrigo.modelo.ParticipantId;
import java.util.List;
import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<Participant, ParticipantId> {

    int countByGroupIdGroup(Integer groupId);

    List<Participant> findByGroupIdGroup(Integer groupId);

    Participant findByUserIdUserAndGroupIdGroup(Integer userId, Integer groupId);

    boolean existsByUserIdUser(Integer userId);

    Optional<Participant> findByUserIdUser(Integer userId);
}