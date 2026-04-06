package com.rodrigo.controlador;

import com.rodrigo.modelo.*;
import com.rodrigo.repositorio.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/requests")
public class RequestController {

    @Autowired
    private RequestRepository requestRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GameReunionRepository groupRepository;

    @Autowired
    private ParticipantRepository participantRepository;

    // ── Enviar solicitud ──────────────────────────────────────────────────────

    @PostMapping("/send")
    public ResponseEntity<?> sendRequest(@RequestParam Integer userId,
                                          @RequestParam Integer groupId) {
        try {
            // Verificar que el grupo existe y es de tipo SOLICITUD
            GameReunion group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("Grupo no encontrado."));

            if (group.getPrivacy() != Privacy.SOLICITUD) {
                return ResponseEntity.badRequest()
                    .body("Este grupo no acepta solicitudes.");
            }

            // Verificar que el usuario no está ya en un grupo
            if (participantRepository.existsByUserIdUser(userId)) {
                return ResponseEntity.badRequest()
                    .body("Ya estás en un grupo.");
            }

            // Verificar que no hay solicitud pendiente ya
            Request existing = requestRepository
                .findByUserIdUserAndGroupIdGroup(userId, groupId);
            if (existing != null && existing.getStatus() == RequestStatus.PENDIENTE) {
                return ResponseEntity.badRequest()
                    .body("Ya tienes una solicitud pendiente para este grupo.");
            }

            User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado."));

            Request request = new Request();
            request.setUser(user);
            request.setGroup(group);
            request.setStatus(RequestStatus.PENDIENTE);

            requestRepository.save(request);
            return ResponseEntity.ok("Solicitud enviada correctamente.");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ── Listar solicitudes pendientes de un grupo (para el líder) ─────────────

    @GetMapping("/group/{groupId}")
    public ResponseEntity<?> getGroupRequests(@PathVariable Integer groupId) {
        List<Request> requests = requestRepository
            .findByGroupIdGroupAndStatus(groupId, RequestStatus.PENDIENTE);

        List<RequestDTO> dtos = requests.stream()
            .map(RequestDTO::new)
            .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    // ── Aceptar solicitud ─────────────────────────────────────────────────────

    @PostMapping("/{requestId}/accept")
    public ResponseEntity<?> acceptRequest(@PathVariable Integer requestId,
                                            @RequestParam Integer leaderId) {
        try {
            Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada."));

            // Verificar que quien acepta es líder o admin del grupo
            Participant leader = participantRepository
                .findByUserIdUserAndGroupIdGroup(
                    leaderId, request.getGroup().getIdGroup());
            if (leader == null || leader.getRole() == Role.MIEMBRO) {
                return ResponseEntity.badRequest()
                    .body("No tienes permisos para aceptar solicitudes.");
            }

            // Verificar que el grupo no está lleno
            int current = participantRepository
                .countByGroupIdGroup(request.getGroup().getIdGroup());
            if (current >= request.getGroup().getMaxPlayers()) {
                return ResponseEntity.badRequest().body("El grupo está lleno.");
            }

            // Añadir al usuario como miembro
            Participant participant = new Participant();
            participant.setUser(request.getUser());
            participant.setGroup(request.getGroup());
            participant.setRole(Role.MIEMBRO);
            participantRepository.save(participant);

            // Actualizar solicitud
            User decider = userRepository.findById(leaderId).orElse(null);
            request.setStatus(RequestStatus.ACEPTADO);
            request.setDecidedBy(decider);
            requestRepository.save(request);

            return ResponseEntity.ok("Solicitud aceptada.");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ── Rechazar solicitud ────────────────────────────────────────────────────

    @PostMapping("/{requestId}/reject")
    public ResponseEntity<?> rejectRequest(@PathVariable Integer requestId,
                                            @RequestParam Integer leaderId) {
        try {
            Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Solicitud no encontrada."));

            Participant leader = participantRepository
                .findByUserIdUserAndGroupIdGroup(
                    leaderId, request.getGroup().getIdGroup());
            if (leader == null || leader.getRole() == Role.MIEMBRO) {
                return ResponseEntity.badRequest()
                    .body("No tienes permisos para rechazar solicitudes.");
            }

            User decider = userRepository.findById(leaderId).orElse(null);
            request.setStatus(RequestStatus.RECHAZADO);
            request.setDecidedBy(decider);
            requestRepository.save(request);

            return ResponseEntity.ok("Solicitud rechazada.");

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ── DTO ───────────────────────────────────────────────────────────────────

    public static class RequestDTO {
        public Integer id;
        public String username;
        public Integer userId;
        public String status;
        public String createdAt;

        public RequestDTO(Request r) {
            this.id = r.getId();
            this.username = r.getUser().getUsername();
            this.userId = r.getUser().getIdUser();
            this.status = r.getStatus().name();
            this.createdAt = r.getCreatedAt().toString();
        }
    }
}