package com.rodrigo.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import com.rodrigo.modelo.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    // Buscar usuario por username (para login)
    Optional<User> findByUsername(String username);

    // Verificar si existe un username
    boolean existsByUsername(String username);

    // Opcional: buscar por email
    Optional<User> findByEmail(String email);
}
