package com.grupocordillera.gc_auth.controllers;

import com.grupocordillera.gc_auth.config.JwtUtil;
import com.grupocordillera.gc_auth.dtos.UsuarioRequestDTO;
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

    @GetMapping
    public ResponseEntity<?> listarUsuarios(@RequestHeader("Authorization") String token) {
        try {
            if (!tienePermisoGerencial(token)) {
                return new ResponseEntity<>("ACCESO DENEGADO: Permisos insuficientes.", HttpStatus.FORBIDDEN);
            }
            return ResponseEntity.ok(authService.obtenerTodosLosUsuarios());
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/crear")
    public ResponseEntity<?> crearUsuario(@RequestHeader("Authorization") String token,
            @RequestBody UsuarioRequestDTO dto) {
        try {
            if (!tienePermisoGerencial(token)) {
                return new ResponseEntity<>("ACCESO DENEGADO: Permisos insuficientes.", HttpStatus.FORBIDDEN);
            }

            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setEmail(dto.getEmail());
            nuevoUsuario.setPassword(dto.getPassword());
            nuevoUsuario.setNombre(dto.getNombre());
            nuevoUsuario.setRol(dto.getRol());

            Usuario creado = authService.crearUsuario(nuevoUsuario);
            return new ResponseEntity<>(creado, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/modificar/{id}")
    public ResponseEntity<?> modificarUsuario(@RequestHeader("Authorization") String token, @PathVariable Long id,
            @RequestBody UsuarioRequestDTO dto) {
        try {
            if (!tienePermisoGerencial(token)) {
                return new ResponseEntity<>("ACCESO DENEGADO: Permisos insuficientes.", HttpStatus.FORBIDDEN);
            }

            Usuario datosNuevos = new Usuario();
            datosNuevos.setPassword(dto.getPassword());
            datosNuevos.setNombre(dto.getNombre());
            datosNuevos.setRol(dto.getRol());

            Usuario modificado = authService.modificarUsuario(id, datosNuevos);
            return ResponseEntity.ok(modificado);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // 🚀 NUEVA VALIDACIÓN: Flexible y a prueba de errores de expiración
    private boolean tienePermisoGerencial(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            try {
                String jwt = token.substring(7);
                String rol = jwtUtil.extraerRol(jwt);
                
                if (rol == null) return false;
                
                // Convertimos todo a mayúsculas para comparar sin importar cómo se guardó en BD
                String rolMayusculas = rol.toUpperCase();
                return rolMayusculas.equals("ADMIN") || rolMayusculas.equals("GERENTE");
                
            } catch (Exception e) {
                // Si el token expiró o está corrupto, la librería de JWT lanzará un error.
                // Lo atrapamos aquí y denegamos el acceso silenciosamente.
                System.out.println("❌ Error procesando Token: " + e.getMessage());
                return false; 
            }
        }
        return false;
    }
}