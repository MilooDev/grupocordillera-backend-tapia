package com.grupocordillera.gc_auth; // Ajusta el paquete si lo tienes en otra carpeta

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.grupocordillera.gc_auth.models.Usuario;
import com.grupocordillera.gc_auth.models.RolUsuario;
import com.grupocordillera.gc_auth.repositories.UsuarioRepository;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner initDatabase(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (usuarioRepository.count() == 0) {
                
                // 1. Crear Gerente
                Usuario gerente = new Usuario();
                gerente.setEmail("gerente@cordillera.cl");
                gerente.setPassword(passwordEncoder.encode("1234"));
                gerente.setNombre("Gerente General");
                gerente.setRol(RolUsuario.GERENTE); 
                usuarioRepository.save(gerente);

                // 2. Crear Admin
                Usuario admin = new Usuario();
                admin.setEmail("admin@cordillera.cl");
                admin.setPassword(passwordEncoder.encode("1234"));
                admin.setNombre("Administrador Sistema");
                admin.setRol(RolUsuario.ADMIN); 
                usuarioRepository.save(admin);

                // 3. Crear Cajero
                Usuario cajero = new Usuario();
                cajero.setEmail("cajero@cordillera.cl");
                cajero.setPassword(passwordEncoder.encode("1234"));
                cajero.setNombre("Cajero Principal");
                cajero.setRol(RolUsuario.CAJERO); 
                usuarioRepository.save(cajero);

                // 4. Crear Bodeguero
                Usuario bodeguero = new Usuario();
                bodeguero.setEmail("bodeguero@cordillera.cl");
                bodeguero.setPassword(passwordEncoder.encode("1234"));
                bodeguero.setNombre("Jefe de Bodega");
                bodeguero.setRol(RolUsuario.BODEGUERO); 
                usuarioRepository.save(bodeguero);

                System.out.println("✅ Usuarios de prueba (Gerente, Admin, Cajero, Bodeguero) creados exitosamente.");
            }
        };
    }
}