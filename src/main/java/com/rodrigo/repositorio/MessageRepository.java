package com.rodrigo.repositorio;

import com.rodrigo.modelo.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByGroupIdGroupOrderByTimestampAsc(Integer groupId);
}