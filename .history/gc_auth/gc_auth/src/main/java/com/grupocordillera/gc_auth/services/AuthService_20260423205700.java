package com.grupocordillera.gc_auth.services;

import com.grupocordillera.gc_auth.models.Usuario;
import com.grupocordillera.gc_auth.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Usuario registrarUsuario(Usuario usuario) throws Exception {
        // 1. Verificamos si el correo ya existe
        if (usuarioRepository.existsByEmail(usuario.getEmail())) {
            throw new Exception("El correo ya está registrado");
        }

        // 2. Encriptamos la contraseña (Ej: "123456" se vuelve "$2a$10$xyz...")
        String claveEncriptada = passwordEncoder.encode(usuario.getPassword());
        usuario.setPassword(claveEncriptada);

        // 3. Guardamos en la base de datos
        return usuarioRepository.save(usuario);
    }
}