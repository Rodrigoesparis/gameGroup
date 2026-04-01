package com.rodrigo.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.rodrigo.modelo.*;
import com.rodrigo.servicio.ParticipantService;
import com.rodrigo.repositorio.ParticipantRepository;
import java.util.List;

@RestController
@RequestMapping("/participants")
public class ParticipantController {

    @Autowired
    private ParticipantService participantService;

    @Autowired
    private ParticipantRepository participantRepository;

    // ── Unirse a un grupo ─────────────────────────────────────────────────────

    @PostMapping("/join")
    public ResponseEntity<?> joinGroup(@RequestParam Integer userId,
                                        @RequestParam Integer groupId,
                                        @RequestParam(required = false) String password) {
        try {
            Participant p = participantService.unirseAlGrupo(userId, groupId, password);
            return ResponseEntity.ok("Te has unido al grupo correctamente.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ── Salir de un grupo ─────────────────────────────────────────────────────

    @DeleteMapping("/leave")
    public ResponseEntity<?> leaveGroup(@RequestParam Integer userId,
                                         @RequestParam Integer groupId) {
        try {
            participantService.salirDelGrupo(userId, groupId);
            return ResponseEntity.ok("Has salido del grupo.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ── Expulsar a un usuario (líder o admin) ─────────────────────────────────
    // Líder puede expulsar a cualquiera
    // Admin solo puede expulsar a miembros

    @DeleteMapping("/kick")
    public ResponseEntity<?> kickUser(@RequestParam Integer requesterId,
                                       @RequestParam Integer targetId,
                                       @RequestParam Integer groupId) {
        try {
            Participant requester = participantRepository.findByUserIdUserAndGroupIdGroup(requesterId, groupId);
            Participant target = participantRepository.findByUserIdUserAndGroupIdGroup(targetId, groupId);

            if (requester == null) {
                return ResponseEntity.badRequest().body("No estás en este grupo.");
            }
            if (target == null) {
                return ResponseEntity.badRequest().body("El usuario a expulsar no está en este grupo.");
            }

            // Miembro no puede expulsar a nadie
            if (requester.getRole() == Role.MIEMBRO) {
                return ResponseEntity.badRequest().body("No tienes permisos para expulsar a nadie.");
            }

            // Admin solo puede expulsar a miembros
            if (requester.getRole() == Role.ADMIN && target.getRole() != Role.MIEMBRO) {
                return ResponseEntity.badRequest().body("Un admin solo puede expulsar a miembros.");
            }

            // Nadie puede expulsar al líder
            if (target.getRole() == Role.LIDER) {
                return ResponseEntity.badRequest().body("No puedes expulsar al líder.");
            }

            participantService.expulsarUsuario(targetId, groupId);
            return ResponseEntity.ok("Usuario expulsado del grupo.");

        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ── Listar participantes de un grupo ──────────────────────────────────────

    @GetMapping("/{groupId}")
    public ResponseEntity<?> listParticipants(@PathVariable Integer groupId) {
        try {
            List<Participant> participants = participantService.listarParticipantes(groupId);
            return ResponseEntity.ok(participants);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ── Ver en qué grupo está un usuario ─────────────────────────────────────

    @GetMapping("/user/{userId}")
public ResponseEntity<?> getUserGroup(@PathVariable Integer userId) {
    Participant p = participantService.obtenerGrupoDeUsuario(userId);
    if (p == null) {
        return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(p);
}
}