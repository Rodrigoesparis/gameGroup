package com.rodrigo.servicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.rodrigo.modelo.*;
import com.rodrigo.repositorio.*;
import java.util.List;

@Service
public class ParticipantService {

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameGroupRepository groupRepository;

    @Autowired
    private GameGroupService gameGroupService;

    // ── Unirse a un grupo ─────────────────────────────────────────────────────

    public Participant unirseAlGrupo(Integer userId, Integer groupId, String passwordIntentada) {

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

        GameGroup group = groupRepository.findById(groupId)
            .orElseThrow(() -> new IllegalArgumentException("Grupo no encontrado."));

        // Un usuario solo puede estar en un grupo a la vez
        if (participantRepository.existsByUserIdUser(userId)) {
            throw new IllegalStateException("Ya estás en un grupo. Sal primero antes de unirte a otro.");
        }

        // Verificar que hay sitio
        int actuales = participantRepository.countByGroupIdGroup(groupId);
        if (group.getMaxPlayers() != null && actuales >= group.getMaxPlayers()) {
            throw new IllegalStateException("El grupo está lleno.");
        }

        // Lógica según privacidad del grupo
        switch (group.getPrivacy()) {
            case ABIERTO:
                break;

            case PRIVADO_PASSWORD:
                if (passwordIntentada == null || !passwordIntentada.equals(group.getPassword())) {
                    throw new IllegalArgumentException("Contraseña incorrecta.");
                }
                break;

            case INVITACION:
                // Fase futura: comprobar si hay invitación pendiente
                throw new IllegalStateException("Este grupo solo acepta miembros por invitación.");

            case SOLICITUD:
                // Fase futura: crear Request automáticamente
                throw new IllegalStateException("Este grupo requiere solicitud. Usa el sistema de solicitudes.");
        }

        Participant participant = new Participant();
        participant.setUser(user);
        participant.setGroup(group);
        participant.setRole(Role.MIEMBRO);

        return participantRepository.save(participant);
    }

    // ── Salir de un grupo ─────────────────────────────────────────────────────

    public void salirDelGrupo(Integer userId, Integer groupId) {
        Participant participant = participantRepository.findByUserIdUserAndGroupIdGroup(userId, groupId);

        if (participant == null) {
            throw new IllegalArgumentException("No estás en ese grupo.");
        }

        boolean eraLider = participant.getRole() == Role.LIDER;

        participantRepository.delete(participant);

        if (eraLider) {
            int restantes = participantRepository.countByGroupIdGroup(groupId);
            if (restantes > 0) {
                // Transferir liderazgo: primero a un admin, si no al primer miembro
                gameGroupService.transferirLiderazgoAlSiguiente(groupId, userId);
            } else {
                // Nadie queda → el grupo desaparece
                groupRepository.deleteById(groupId);
            }
        }
    }

    // ── Expulsar a un usuario (la lógica de permisos está en el controlador) ──

    public void expulsarUsuario(Integer targetId, Integer groupId) {
        Participant participant = participantRepository.findByUserIdUserAndGroupIdGroup(targetId, groupId);

        if (participant == null) {
            throw new IllegalArgumentException("El usuario no está en ese grupo.");
        }

        participantRepository.delete(participant);
    }

    // ── Listar participantes de un grupo ──────────────────────────────────────

    public List<Participant> listarParticipantes(Integer groupId) {
        if (!groupRepository.existsById(groupId)) {
            throw new IllegalArgumentException("Grupo no encontrado.");
        }
        return participantRepository.findByGroupIdGroup(groupId);
    }

    // ── Obtener el grupo actual de un usuario ─────────────────────────────────

    public Participant obtenerGrupoDeUsuario(Integer userId) {
        return participantRepository.findByUserIdUser(userId).orElse(null);
    }
}