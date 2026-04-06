package com.rodrigo.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import com.rodrigo.modelo.GameReunion;
import java.util.List;

public interface GameReunionRepository extends JpaRepository<GameReunion, Integer> {

    // Listar grupos por tipo de privacidad
    List<GameReunion> findByPrivacy(com.rodrigo.modelo.Privacy privacy);

    // Buscar grupos creados por un usuario
    List<GameReunion> findByCreatorIdUser(Integer idUser);
}
