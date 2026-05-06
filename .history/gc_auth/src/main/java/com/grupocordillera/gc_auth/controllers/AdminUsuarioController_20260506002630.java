package com.grupocordillera.gc_auth.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/usuarios")
public class AdminUsuarioController {

    // Simulación de tu Servicio de Usuarios (debes inyectar el tuyo)
    // @Autowired
    // private UsuarioService usuarioService;

    // 1. CREAR NUEVO USUARIO (Vendedor o Sub-Admin)
    @PostMapping("/crear")
    public ResponseEntity<?> crearUsuario(@RequestHeader("Authorization") String token,
            @RequestBody Object nuevoUsuarioDTO) {
        // Aquí verificas si el token pertenece a un ADMIN
        if (!esAdmin(token)) {
            return new ResponseEntity<>("ACCESO DENEGADO: Solo los dueños/admins pueden crear usuarios.",
                    HttpStatus.FORBIDDEN);
        }

        // Lógica para guardar el usuario en tu base de datos
        // usuarioService.crearUsuario(nuevoUsuarioDTO);

        return new ResponseEntity<>("Usuario creado exitosamente por el Administrador", HttpStatus.CREATED);
    }

    // 2. MODIFICAR USUARIO (Cambiar rol, nombre, sucursal, etc)
    @PutMapping("/modificar/{id}")
    public ResponseEntity<?> modificarUsuario(@RequestHeader("Authorization") String token, @PathVariable Long id,
            @RequestBody Object datosModificados) {
        if (!esAdmin(token)) {
            return new ResponseEntity<>("ACCESO DENEGADO.", HttpStatus.FORBIDDEN);
        }

        // Lógica de actualización
        return new ResponseEntity<>("Usuario modificado exitosamente", HttpStatus.OK);
    }

    // Método utilitario (Si usas Spring Security + JWT, esto se hace automático con
    // @PreAuthorize)
    private boolean esAdmin(String token) {
        // Lógica ficticia: Aquí usarías tu JwtUtil para extraer el Rol del token
        // return jwtUtil.extraerRol(token).equals("ADMIN");
        return true;
    }
}