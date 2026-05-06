package com.grupocordillera.gc_auth.services;

import com.grupocordillera.gc_auth.config.JwtUtil;
import com.grupocordillera.gc_auth.models.Usuario;
import com.grupocordillera.gc_auth.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    // 1. Lógica de Registro
    public Usuario registrarUsuario(Usuario usuario) throws Exception {
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new Exception("El correo ya está registrado en el sistema");
        }
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setActivo(true); // Aseguramos que el usuario nazca activo

        return usuarioRepository.save(usuario);
    }

    // 2. Lógica de Login
    public String autenticar(String email, String passwordPlana) throws Exception {
        // Buscamos al usuario por su correo
        Optional<Usuario> usuarioOpt = usuarioRepository.findByEmail(email);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();

            if (passwordEncoder.matches(passwordPlana, usuario.getPassword())) {

                return jwtUtil.generateToken(usuario.getEmail(), usuario.getRol().toString());
            }
        }

        throw new Exception("Credenciales incorrectas. Acceso denegado.");
    }
}