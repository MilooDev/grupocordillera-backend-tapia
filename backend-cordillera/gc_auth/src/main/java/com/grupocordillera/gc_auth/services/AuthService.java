package com.grupocordillera.gc_auth.services;

import com.grupocordillera.gc_auth.config.JwtUtil;
import com.grupocordillera.gc_auth.dtos.LoginRequestDTO;
import com.grupocordillera.gc_auth.models.Usuario;
import com.grupocordillera.gc_auth.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;

    public String login(String email, String rawPassword) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);
        if (usuarioOpt.isEmpty()) {
            throw new RuntimeException("Usuario no encontrado");
        }

        Usuario usuario = usuarioOpt.get();
        if (!passwordEncoder.matches(rawPassword, usuario.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        return jwtUtil.generarToken(usuario.getEmail(), usuario.getRol().name());
    }

    // 🚀 NUEVO: Retorna la lista a la tabla del frontend
    public List<Usuario> obtenerTodosLosUsuarios() {
        return usuarioRepository.findAll();
    }

    public Usuario crearUsuario(Usuario nuevoUsuario) {
        if (usuarioRepository.findByEmail(nuevoUsuario.getEmail()).isPresent()) {
            throw new RuntimeException("El correo ya está registrado");
        }
        nuevoUsuario.setPassword(passwordEncoder.encode(nuevoUsuario.getPassword()));
        return usuarioRepository.save(nuevoUsuario);
    }

    public Usuario modificarUsuario(Long id, Usuario datosNuevos) {
        Usuario existente = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("El usuario no existe"));

        existente.setNombre(datosNuevos.getNombre());
        existente.setRol(datosNuevos.getRol());

        if (datosNuevos.getPassword() != null && !datosNuevos.getPassword().isEmpty()) {
            existente.setPassword(passwordEncoder.encode(datosNuevos.getPassword()));
        }

        return usuarioRepository.save(existente);
    }

    public Object login(LoginRequestDTO any) {
        throw new UnsupportedOperationException("Unimplemented method 'login'");
    }
}