package com.grupocordillera.gc_auth.controllers;

import com.grupocordillera.gc_auth.config.JwtUtil;
import com.grupocordillera.gc_auth.models.RolUsuario;
import com.grupocordillera.gc_auth.models.Usuario;
import com.grupocordillera.gc_auth.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/usuarios")
public class AdminUsuarioController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthService authService;

    @PostMapping("/crear")
    public ResponseEntity<?> crearUsuario(@RequestHeader("Authorization") String token,
            @RequestBody Usuario nuevoUsuario) {
        try {
            if (!esAdmin(token)) {
                return new ResponseEntity<>("ACCESO DENEGADO: Solo los dueños/admins pueden crear usuarios.",
                        HttpStatus.FORBIDDEN);
            }
            Usuario creado = authService.crearUsuario(nuevoUsuario);
            return new ResponseEntity<>(creado, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/modificar/{id}")
    public ResponseEntity<?> modificarUsuario(@RequestHeader("Authorization") String token, @PathVariable Long id,
            @RequestBody Usuario datosNuevos) {
        try {
            if (!esAdmin(token)) {
                return new ResponseEntity<>("ACCESO DENEGADO.", HttpStatus.FORBIDDEN);
            }
            Usuario modificado = authService.modificarUsuario(id, datosNuevos);
            return ResponseEntity.ok(modificado);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    private boolean esAdmin(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            String jwt = token.substring(7);
            String rol = jwtUtil.extraerRol(jwt);
            return RolUsuario.ROLE_ADMIN.name().equals(rol);
        }
        return false;
    }
}