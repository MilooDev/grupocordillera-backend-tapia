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
@CrossOrigin(origins = "*") // Permite que Angular se conecte después
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/registro")
    public ResponseEntity<?> registrar(@Valid @RequestBody Usuario usuario, BindingResult validaciones) {

        // 1. Escudo de Validaciones: Si el JSON tiene errores (ej: teléfono sin 9)
        if (validaciones.hasErrors()) {
            Map<String, String> errores = new HashMap<>();
            validaciones.getFieldErrors().forEach(err -> errores.put(err.getField(), err.getDefaultMessage()));
            return ResponseEntity.badRequest().body(errores);
        }

        // 2. Lógica de registro exitoso
        try {
            Usuario nuevoUsuario = authService.registrarUsuario(usuario);
            // Ocultamos la contraseña encriptada por seguridad antes de devolver el JSON
            nuevoUsuario.setPassword("******");
            return new ResponseEntity<>(nuevoUsuario, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}