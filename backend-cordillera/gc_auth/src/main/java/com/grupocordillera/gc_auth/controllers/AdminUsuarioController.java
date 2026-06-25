package com.grupocordillera.gc_auth.controllers;

import com.grupocordillera.gc_auth.config.JwtUtil;
import com.grupocordillera.gc_auth.dtos.UsuarioRequestDTO;
import com.grupocordillera.gc_auth.models.Usuario;
import com.grupocordillera.gc_auth.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/usuarios")
// 🚀 ELIMINAMOS @CrossOrigin PARA DEJAR QUE EL API GATEWAY HAGA SU TRABAJO SOLO
public class AdminUsuarioController {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private AuthService authService;

    @GetMapping
    public ResponseEntity<?> listarUsuarios(@RequestHeader("Authorization") String token) {
        String estado = verificarPermisos(token);
        if (!estado.equals("OK")) {
            System.out.println("❌ Bloqueo en GET: " + estado);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "ACCESO DENEGADO", "motivo", estado));
        }
        return ResponseEntity.ok(authService.obtenerTodosLosUsuarios());
    }

    @PostMapping("/crear")
    public ResponseEntity<?> crearUsuario(@RequestHeader("Authorization") String token,
            @RequestBody UsuarioRequestDTO dto) {
        String estado = verificarPermisos(token);
        if (!estado.equals("OK")) {
            System.out.println("❌ Bloqueo en POST: " + estado);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "ACCESO DENEGADO", "motivo", estado));
        }

        try {
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setEmail(dto.getEmail());
            nuevoUsuario.setPassword(dto.getPassword());
            nuevoUsuario.setNombre(dto.getNombre());
            nuevoUsuario.setRol(dto.getRol());

            Usuario creado = authService.crearUsuario(nuevoUsuario);
            return new ResponseEntity<>(creado, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/modificar/{id}")
    public ResponseEntity<?> modificarUsuario(@RequestHeader("Authorization") String token, @PathVariable Long id,
            @RequestBody UsuarioRequestDTO dto) {
        String estado = verificarPermisos(token);
        if (!estado.equals("OK")) {
            System.out.println("❌ Bloqueo en PUT: " + estado);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "ACCESO DENEGADO", "motivo", estado));
        }

        try {
            Usuario datosNuevos = new Usuario();
            datosNuevos.setPassword(dto.getPassword());
            datosNuevos.setNombre(dto.getNombre());
            datosNuevos.setRol(dto.getRol());

            Usuario modificado = authService.modificarUsuario(id, datosNuevos);
            return ResponseEntity.ok(modificado);
        } catch (Exception e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    private String verificarPermisos(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return "El token es nulo o no tiene el prefijo 'Bearer '";
        }
        try {
            String jwt = token.substring(7);
            String rol = jwtUtil.extraerRol(jwt);
            
            if (rol == null) return "El token es válido pero no contiene la variable de Rol";
            
            String rolMayusculas = rol.toUpperCase();
            if (rolMayusculas.equals("ADMIN") || rolMayusculas.equals("GERENTE")) {
                return "OK";
            } else {
                return "Tu rol es insuficiente. Tienes rol: " + rol;
            }
        } catch (Exception e) {
            return "El Token expiró o la firma secreta no coincide: " + e.getMessage();
        }
    }
}