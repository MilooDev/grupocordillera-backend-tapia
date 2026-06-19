package com.grupocordillera.gc_auth; 

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.grupocordillera.gc_auth.models.Usuario;
import com.grupocordillera.gc_auth.models.RolUsuario;
import com.grupocordillera.gc_auth.repositories.UsuarioRepository;

@Configuration
public class DataSeeder {

    // Extraemos la contraseña desde el entorno (o usamos un fallback seguro si no existe)
    @Value("${SEEDER_PASSWORD:CordilleraSegura2026!}")
    private String seederPassword;

    @Bean
    CommandLineRunner initDatabase(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (usuarioRepository.count() == 0) {
                
                // 1. Crear Gerente
                Usuario gerente = new Usuario();
                gerente.setEmail("gerente@cordillera.cl");
                gerente.setPassword(passwordEncoder.encode(seederPassword));
                gerente.setNombre("Gerente General");
                gerente.setRol(RolUsuario.GERENTE); 
                usuarioRepository.save(gerente);

                // 2. Crear Admin
                Usuario admin = new Usuario();
                admin.setEmail("admin@cordillera.cl");
                admin.setPassword(passwordEncoder.encode(seederPassword));
                admin.setNombre("Administrador Sistema");
                admin.setRol(RolUsuario.ADMIN); 
                usuarioRepository.save(admin);

                // 3. Crear Cajero
                Usuario cajero = new Usuario();
                cajero.setEmail("cajero@cordillera.cl");
                cajero.setPassword(passwordEncoder.encode(seederPassword));
                cajero.setNombre("Cajero Principal");
                cajero.setRol(RolUsuario.CAJERO); 
                usuarioRepository.save(cajero);

                // 4. Crear Bodeguero
                Usuario bodeguero = new Usuario();
                bodeguero.setEmail("bodeguero@cordillera.cl");
                bodeguero.setPassword(passwordEncoder.encode(seederPassword));
                bodeguero.setNombre("Jefe de Bodega");
                bodeguero.setRol(RolUsuario.BODEGUERO); 
                usuarioRepository.save(bodeguero);

                System.out.println("✅ Usuarios de prueba creados exitosamente usando variables de entorno.");
            }
        };
    }
}