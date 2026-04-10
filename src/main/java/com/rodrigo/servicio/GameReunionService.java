package com.rodrigo.servicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.rodrigo.controlador.GroupEventController;
import com.rodrigo.modelo.*;
import com.rodrigo.repositorio.*;
import java.util.List;

@Service
public class GameReunionService {

    @Autowired
    private GameReunionRepository groupRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
private GroupEventController groupEventController;

    // ── Crear grupo ───────────────────────────────────────────────────────────

    public GameReunion crearGrupo(Integer creatorId, String name, String game,
                                 String mode, Privacy privacy, String password,
                                 Integer maxPlayers) {

        User creator = userRepository.findById(creatorId)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

        if (participantRepository.existsByUserIdUser(creatorId)) {
            throw new IllegalStateException("Ya estás en un grupo. Sal primero antes de crear uno nuevo.");
        }

        if (privacy == Privacy.PRIVADO_PASSWORD && (password == null || password.isBlank())) {
            throw new IllegalArgumentException("Un grupo privado con contraseña debe tener una contraseña.");
        }

        GameReunion group = new GameReunion();
        group.setName(name);
        group.setGame(game);
        group.setMode(mode);
        group.setPrivacy(privacy);
        group.setPassword(password);
        group.setMaxPlayers(maxPlayers);
        group.setCreator(creator);

        group = groupRepository.save(group);

        // El creador entra automáticamente como LIDER
        Participant lider = new Participant();
        lider.setUser(creator);
        lider.setGroup(group);
        lider.setRole(Role.LIDER);
        participantRepository.save(lider);

        groupEventController.notifyGroupUpdate(group.getIdGroup(), "CREATED");

        return group;
    }

    // ── Listar grupos con info extra ──────────────────────────────────────────

    public List<GameReunionDTO> listarGruposConInfo() {
        return groupRepository.findAll().stream().map(group -> {
            int currentPlayers = participantRepository.countByGroupIdGroup(group.getIdGroup());
            return new GameReunionDTO(group, currentPlayers);
        }).toList();
    }

    public List<GameReunion> listarGrupos() {
        return groupRepository.findAll();
    }

    public GameReunion buscarPorId(Integer groupId) {
        return groupRepository.findById(groupId)
            .orElseThrow(() -> new IllegalArgumentException("Grupo no encontrado."));
    }

    // ── Actualizar info del grupo (líder o admin) ─────────────────────────────

    public GameReunion actualizarGrupo(Integer groupId, String name, String game,
                                      String mode, Privacy privacy,
                                      String password, Integer maxPlayers) {

        GameReunion group = buscarPorId(groupId);

        if (name != null && !name.isBlank())     group.setName(name);
        if (game != null && !game.isBlank())     group.setGame(game);
        if (mode != null && !mode.isBlank())     group.setMode(mode);
        if (privacy != null)                     group.setPrivacy(privacy);
        if (password != null)                    group.setPassword(password);
        if (maxPlayers != null)                  group.setMaxPlayers(maxPlayers);

        return groupRepository.save(group);
    }

    // ── Cambiar rol de un participante ────────────────────────────────────────

    public void cambiarRol(Integer groupId, Integer targetUserId, Role nuevoRol) {
        Participant target = participantRepository.findByUserIdUserAndGroupIdGroup(targetUserId, groupId);
        if (target == null) {
            throw new IllegalArgumentException("El usuario no está en el grupo.");
        }
        if (target.getRole() == Role.LIDER) {
            throw new IllegalStateException("No puedes cambiar el rol del líder. Usa transferir liderazgo.");
        }
        target.setRole(nuevoRol);
        participantRepository.save(target);
    }

    // ── Transferir liderazgo manualmente ─────────────────────────────────────

    public void transferirLiderazgo(Integer groupId, Integer liderActualId, Integer nuevoLiderId) {
        Participant liderActual = participantRepository.findByUserIdUserAndGroupIdGroup(liderActualId, groupId);
        if (liderActual == null || liderActual.getRole() != Role.LIDER) {
            throw new IllegalStateException("Solo el líder puede transferir el liderazgo.");
        }

        Participant nuevoLider = participantRepository.findByUserIdUserAndGroupIdGroup(nuevoLiderId, groupId);
        if (nuevoLider == null) {
            throw new IllegalArgumentException("El usuario al que quieres ceder el liderazgo no está en el grupo.");
        }

        liderActual.setRole(Role.MIEMBRO);
        nuevoLider.setRole(Role.LIDER);

        participantRepository.save(liderActual);
        participantRepository.save(nuevoLider);
    }

    // ── Transferencia automática cuando el líder se va ────────────────────────

    public void transferirLiderazgoAlSiguiente(Integer groupId, Integer liderQueSeVaId) {
        List<Participant> miembros = participantRepository.findByGroupIdGroup(groupId);

        // Primero a un admin, si no al primer miembro disponible
        Participant nuevoLider = miembros.stream()
            .filter(p -> !p.getUser().getIdUser().equals(liderQueSeVaId))
            .filter(p -> p.getRole() == Role.ADMIN)
            .findFirst()
            .orElse(
                miembros.stream()
                    .filter(p -> !p.getUser().getIdUser().equals(liderQueSeVaId))
                    .findFirst()
                    .orElse(null)
            );

        if (nuevoLider != null) {
            nuevoLider.setRole(Role.LIDER);
            participantRepository.save(nuevoLider);
        }
    }

    // ── Eliminar grupo (solo líder) ───────────────────────────────────────────

    public void eliminarGrupo(Integer groupId, Integer requesterId) {
        Participant solicitante = participantRepository.findByUserIdUserAndGroupIdGroup(requesterId, groupId);
        if (solicitante == null || solicitante.getRole() != Role.LIDER) {
            throw new IllegalStateException("Solo el líder puede eliminar el grupo.");
        }
        groupRepository.deleteById(groupId);
    }

    // ── DTO para listado de grupos ────────────────────────────────────────────

    public static class GameReunionDTO {
        public Integer idGroup;
        public String name;
        public String game;
        public String mode;
        public Privacy privacy;
        public Integer maxPlayers;
        public int currentPlayers;

        public GameReunionDTO(GameReunion g, int current) {
            this.idGroup = g.getIdGroup();
            this.name = g.getName();
            this.game = g.getGame();
            this.mode = g.getMode();
            this.privacy = g.getPrivacy();
            this.maxPlayers = g.getMaxPlayers();
            this.currentPlayers = current;
        }
    }
}