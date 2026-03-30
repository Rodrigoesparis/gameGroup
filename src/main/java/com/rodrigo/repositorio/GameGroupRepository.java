package com.rodrigo.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import com.rodrigo.modelo.GameGroup;
import java.util.List;

public interface GameGroupRepository extends JpaRepository<GameGroup, Integer> {

    // Listar grupos por tipo de privacidad
    List<GameGroup> findByPrivacy(com.rodrigo.modelo.Privacy privacy);

    // Buscar grupos creados por un usuario
    List<GameGroup> findByCreatorIdUser(Integer idUser);
}
