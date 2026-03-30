package com.rodrigo.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.rodrigo.modelo.User;
import com.rodrigo.servicio.UserService;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    // ── Registro ──────────────────────────────────────────────────────────────

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        try {
            User user = userService.registrarUsuario(
                req.name, req.username, req.email, req.password, req.age
            );
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ── Login básico ──────────────────────────────────────────────────────────

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        try {
            User user = userService.login(req.username, req.password);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ── Listar todos (solo para pruebas) ──────────────────────────────────────

    @GetMapping
    public List<User> getAllUsers() {
        return userService.listarTodos();
    }

    // ── Actualizar perfil ─────────────────────────────────────────────────────

    @PutMapping("/{userId}")
    public ResponseEntity<?> updateProfile(@PathVariable Integer userId,
                                            @RequestBody UpdateProfileRequest req) {
        try {
            User updated = userService.actualizarPerfil(userId, req.name, req.email);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ── DTOs (clases internas para recibir JSON) ──────────────────────────────

    static class RegisterRequest {
        public String name;
        public String username;
        public String email;
        public String password;
        public Integer age;
    }

    static class LoginRequest {
        public String username;
        public String password;
    }

    static class UpdateProfileRequest {
        public String name;
        public String email;
    }
}