package com.rodrigo.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import com.rodrigo.modelo.Request;
import com.rodrigo.modelo.RequestStatus;
import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Integer> {

    // Solicitudes pendientes de un grupo
    List<Request> findByGroupIdGroupAndStatus(Integer groupId, RequestStatus status);

    // Solicitudes de un usuario
    List<Request> findByUserIdUser(Integer userId);

    // Buscar solicitud específica
    Request findByUserIdUserAndGroupIdGroup(Integer userId, Integer groupId);
}