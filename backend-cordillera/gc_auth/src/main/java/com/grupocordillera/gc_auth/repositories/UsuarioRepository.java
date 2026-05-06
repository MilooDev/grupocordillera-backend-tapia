package com.grupocordillera.gc_auth.repositories;

import com.grupocordillera.gc_auth.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    // Método clave para buscar al usuario cuando intenta hacer Login
    Optional<Usuario> findByEmail(String email);
}