package com.rodrigo;

import com.rodrigo.modelo.*;
import com.rodrigo.servicio.*;
import com.rodrigo.repositorio.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Component
public class MenuConsola implements CommandLineRunner {

    @Autowired private UserService userService;
    @Autowired private GameGroupService gameGroupService;
    @Autowired private ParticipantService participantService;
    @Autowired private ParticipantRepository participantRepository;

    private User usuarioActivo = null;
    private final Scanner sc = new Scanner(System.in);

    @Override
    public void run(String... args) {
        System.out.println("\n=== GAMEREUNION - CONSOLA ===");

        while (true) {
            if (usuarioActivo == null) {
                menuSinLogin();
            } else {
                menuConLogin();
            }
        }
    }

    // ── Menú sin login ────────────────────────────────────────────────────────

    private void menuSinLogin() {
        System.out.println("\n--- Sin sesión ---");
        System.out.println("1. Registrarse");
        System.out.println("2. Login");
        System.out.println("0. Salir");
        System.out.print("Opción: ");

        switch (sc.nextLine().trim()) {
            case "1" -> registrarse();
            case "2" -> login();
            case "0" -> { System.out.println("Hasta luego."); System.exit(0); }
            default  -> System.out.println("Opción no válida.");
        }
    }

    // ── Menú con login ────────────────────────────────────────────────────────

    private void menuConLogin() {
        Participant miGrupo = participantService.obtenerGrupoDeUsuario(usuarioActivo.getIdUser());

        System.out.println("\n--- Hola, " + usuarioActivo.getUsername() + " ---");

        if (miGrupo == null) {
            // No estoy en ningún grupo
            System.out.println("1. Ver grupos disponibles");
            System.out.println("2. Unirse a un grupo");
            System.out.println("3. Crear un grupo");
            System.out.println("4. Cerrar sesión");
            System.out.print("Opción: ");

            switch (sc.nextLine().trim()) {
                case "1" -> verGrupos();
                case "2" -> unirseAGrupo();
                case "3" -> crearGrupo();
                case "4" -> { usuarioActivo = null; System.out.println("Sesión cerrada."); }
                default  -> System.out.println("Opción no válida.");
            }
        } else {
            // Estoy en un grupo
            GameGroup grupo = miGrupo.getGroup();
            Role rol = miGrupo.getRole();
            System.out.println("Grupo: " + grupo.getName() + " | Juego: " + grupo.getGame()
                    + " | Rol: " + rol);
            System.out.println("1. Ver participantes");
            System.out.println("2. Salir del grupo");

            if (rol == Role.LIDER || rol == Role.ADMIN) {
                System.out.println("3. Modificar grupo");
            }
            if (rol == Role.LIDER) {
                System.out.println("4. Transferir liderazgo");
                System.out.println("5. Ascender miembro a admin");
                System.out.println("6. Expulsar usuario");
                System.out.println("7. Eliminar grupo");
            }
            System.out.println("0. Cerrar sesión");
            System.out.print("Opción: ");

            String op = sc.nextLine().trim();
            switch (op) {
                case "1" -> verParticipantes(grupo.getIdGroup());
                case "2" -> salirDelGrupo(grupo.getIdGroup());
                case "3" -> { if (rol == Role.LIDER || rol == Role.ADMIN) modificarGrupo(grupo.getIdGroup()); }
                case "4" -> { if (rol == Role.LIDER) transferirLiderazgo(grupo.getIdGroup()); }
                case "5" -> { if (rol == Role.LIDER) ascenderAdmin(grupo.getIdGroup()); }
                case "6" -> { if (rol == Role.LIDER) expulsarUsuario(grupo.getIdGroup()); }
                case "7" -> { if (rol == Role.LIDER) eliminarGrupo(grupo.getIdGroup()); }
                case "0" -> { usuarioActivo = null; System.out.println("Sesión cerrada."); }
                default  -> System.out.println("Opción no válida.");
            }
        }
    }

    // ── Acciones ──────────────────────────────────────────────────────────────

    private void registrarse() {
        try {
            System.out.print("Nombre real: ");       String name = sc.nextLine();
            System.out.print("Username: ");          String username = sc.nextLine();
            System.out.print("Email: ");             String email = sc.nextLine();
            System.out.print("Contraseña: ");        String password = sc.nextLine();
            System.out.print("Edad: ");              Integer age = Integer.parseInt(sc.nextLine());

            User u = userService.registrarUsuario(name, username, email, password, age);
            System.out.println("✓ Usuario creado con ID: " + u.getIdUser());
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }

    private void login() {
        try {
            System.out.print("Username: ");   String username = sc.nextLine();
            System.out.print("Contraseña: "); String password = sc.nextLine();

            usuarioActivo = userService.login(username, password);
            System.out.println("✓ Bienvenido, " + usuarioActivo.getName() + "!");
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }

    private void verGrupos() {
        List<GameGroupService.GameGroupDTO> grupos = gameGroupService.listarGruposConInfo();
        if (grupos.isEmpty()) {
            System.out.println("No hay grupos disponibles.");
            return;
        }
        System.out.println("\n--- Grupos disponibles ---");
        for (var g : grupos) {
            System.out.println("[" + g.idGroup + "] " + g.name
                    + " | Juego: " + g.game
                    + " | Modo: " + g.mode
                    + " | " + g.currentPlayers + "/" + g.maxPlayers
                    + " | " + g.privacy);
        }
    }

    private void unirseAGrupo() {
        verGrupos();
        try {
            System.out.print("ID del grupo: "); Integer groupId = Integer.parseInt(sc.nextLine());
            System.out.print("Contraseña (enter si no tiene): "); String pass = sc.nextLine();
            if (pass.isBlank()) pass = null;

            participantService.unirseAlGrupo(usuarioActivo.getIdUser(), groupId, pass);
            System.out.println("✓ Te has unido al grupo.");
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }

    private void crearGrupo() {
        try {
            System.out.print("Nombre del grupo: ");  String name = sc.nextLine();
            System.out.print("Juego: ");             String game = sc.nextLine();
            System.out.print("Modo (COMPETITIVO / CASUAL / PERSONALIZADO): "); String mode = sc.nextLine();
            System.out.print("Privacidad (ABIERTO / PRIVADO_PASSWORD / SOLICITUD / INVITACION): ");
            Privacy privacy = Privacy.valueOf(sc.nextLine().trim().toUpperCase());
            String password = null;
            if (privacy == Privacy.PRIVADO_PASSWORD) {
                System.out.print("Contraseña del grupo: "); password = sc.nextLine();
            }
            System.out.print("Máximo de jugadores: "); Integer maxPlayers = Integer.parseInt(sc.nextLine());

            GameGroup g = gameGroupService.crearGrupo(
                usuarioActivo.getIdUser(), name, game, mode, privacy, password, maxPlayers
            );
            System.out.println("✓ Grupo creado con ID: " + g.getIdGroup());
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }

    private void verParticipantes(Integer groupId) {
        List<Participant> lista = participantService.listarParticipantes(groupId);
        System.out.println("\n--- Participantes ---");
        for (Participant p : lista) {
            System.out.println("- " + p.getUser().getUsername() + " [" + p.getRole() + "]");
        }
    }

    private void salirDelGrupo(Integer groupId) {
        try {
            participantService.salirDelGrupo(usuarioActivo.getIdUser(), groupId);
            System.out.println("✓ Has salido del grupo.");
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }

    private void modificarGrupo(Integer groupId) {
        try {
            System.out.println("(Deja en blanco lo que no quieras cambiar)");
            System.out.print("Nuevo nombre: ");   String name = sc.nextLine();
            System.out.print("Nuevo juego: ");    String game = sc.nextLine();
            System.out.print("Nuevo modo: ");     String mode = sc.nextLine();
            System.out.print("Nueva privacidad (ABIERTO/PRIVADO_PASSWORD/SOLICITUD/INVITACION): ");
            String privStr = sc.nextLine().trim();
            Privacy privacy = privStr.isBlank() ? null : Privacy.valueOf(privStr.toUpperCase());
            System.out.print("Nueva contraseña: "); String pass = sc.nextLine();
            System.out.print("Nuevo máximo jugadores: "); String maxStr = sc.nextLine();
            Integer maxPlayers = maxStr.isBlank() ? null : Integer.parseInt(maxStr);

            gameGroupService.actualizarGrupo(groupId,
                name.isBlank() ? null : name,
                game.isBlank() ? null : game,
                mode.isBlank() ? null : mode,
                privacy,
                pass.isBlank() ? null : pass,
                maxPlayers
            );
            System.out.println("✓ Grupo actualizado.");
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }

    private void transferirLiderazgo(Integer groupId) {
        try {
            verParticipantes(groupId);
            System.out.print("ID del nuevo líder: "); Integer nuevoLiderId = Integer.parseInt(sc.nextLine());
            gameGroupService.transferirLiderazgo(groupId, usuarioActivo.getIdUser(), nuevoLiderId);
            System.out.println("✓ Liderazgo transferido.");
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }

    private void ascenderAdmin(Integer groupId) {
        try {
            verParticipantes(groupId);
            System.out.print("ID del usuario a ascender: "); Integer targetId = Integer.parseInt(sc.nextLine());
            gameGroupService.cambiarRol(groupId, targetId, Role.ADMIN);
            System.out.println("✓ Usuario ascendido a admin.");
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }

    private void expulsarUsuario(Integer groupId) {
        try {
            verParticipantes(groupId);
            System.out.print("ID del usuario a expulsar: "); Integer targetId = Integer.parseInt(sc.nextLine());
            participantService.expulsarUsuario(targetId, groupId);
            System.out.println("✓ Usuario expulsado.");
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }

    private void eliminarGrupo(Integer groupId) {
        try {
            System.out.print("¿Seguro que quieres eliminar el grupo? (s/n): ");
            if (sc.nextLine().trim().equalsIgnoreCase("s")) {
                gameGroupService.eliminarGrupo(groupId, usuarioActivo.getIdUser());
                System.out.println("✓ Grupo eliminado.");
            }
        } catch (Exception e) {
            System.out.println("✗ Error: " + e.getMessage());
        }
    }
}