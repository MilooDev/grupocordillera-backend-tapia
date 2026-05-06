package com.grupocordillera.gc_auth.controllers;

import com.grupocordillera.gc_auth.models.RolUsuario;
import com.grupocordillera.gc_auth.config.JwtUtil; // Asumiendo que tienes esta clase
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/usuarios")
public class AdminUsuarioController {

    @Autowired
    private JwtUtil jwtUtil;

    // Aquí inyectarías tu servicio de usuarios real para guardarlos en BD
    // @Autowired
    // private UsuarioService usuarioService;

    @PostMapping("/crear")
    public ResponseEntity<?> crearEmpleado(@RequestHeader("Authorization") String token,
            @RequestBody Object nuevoUsuarioDTO) {

        // 1. Extraemos el token quitando la palabra "Bearer "
        String jwt = token.substring(7);

        // 2. Verificamos el rol dentro del token
        String rol = jwtUtil.extractRole(jwt);

        if (!rol.equals(RolUsuario.ROLE_ADMIN.name())) {
            return new ResponseEntity<>(
                    "ACCESO DENEGADO: Solo los administradores o dueños pueden crear nuevas cuentas de empleados.",
                    HttpStatus.FORBIDDEN);
        }

        // 3. Lógica para guardar el usuario (Ej:
        // usuarioService.registrar(nuevoUsuarioDTO))

        return new ResponseEntity<>("Cuenta de empleado creada exitosamente.", HttpStatus.CREATED);
    }
}