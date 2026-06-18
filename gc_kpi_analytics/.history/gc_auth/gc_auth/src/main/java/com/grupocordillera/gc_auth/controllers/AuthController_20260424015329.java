package com.grupocordillera.gc_auth.controllers;

import com.grupocordillera.gc_auth.models.Usuario;
import com.grupocordillera.gc_auth.services.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*") // Clave para que tu futuro Frontend en Angular no tenga bloqueos de CORS
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 1. REGISTRO DE USUARIOS
     * Valida los campos (RUT, Telefono, etc.) antes de crear el usuario en
     * PostgreSQL.
     */
    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@Valid @RequestBody Usuario usuario, BindingResult validaciones) {

        // Escudo de Validaciones: Si el JSON no cumple las reglas de Usuario.java
        if (validaciones.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            validaciones.getFieldErrors().forEach(err -> errores.put(err.getField(), err.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errores);
        }

        try {
            Usuario nuevoUsuario = authService.registrarUsuario(usuario);
            // Seteamos la contraseña como oculta en la respuesta JSON por seguridad
            nuevoUsuario.setPassword("******");
            return new ResponseEntity<>(nuevoUsuario, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * 2. LOGIN DE USUARIOS
     * Verifica credenciales y devuelve el Token JWT (El pasaporte digital).
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            // Llama al servicio para validar y generar el token
            String token = authService.autenticar(request.getEmail(), request.getPassword());

            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("token", token);
            respuesta.put("mensaje", "Login exitoso. Bienvenido a Grupo Cordillera.");

            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            // Devuelve 401 Unauthorized si la clave o correo están mal
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    /**
     * 3. OBTENER PERFIL DE USUARIO
     * En AuthController.java (Microservicio GC_Auth)
     */
    @GetMapping("/perfil")
    public ResponseEntity<?> obtenerPerfil() {
        // Si llegas aquí, es porque el Gateway te dejó pasar
        return ResponseEntity.ok("✅ Acceso Concedido: Perfil de usuario de Grupo Cordillera");
    }

    // Clase interna para recibir los datos del Login (Data Transfer Object)
    public static class LoginRequest {
        private String email;
        private String password;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}