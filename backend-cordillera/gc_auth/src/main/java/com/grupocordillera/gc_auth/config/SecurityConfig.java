package com.grupocordillera.gc_auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.disable())
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                // 1. Permitimos el "saludo" inicial de Angular (CORS Preflight)
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                
                // 2. Rutas públicas de Login
                .requestMatchers("/api/auth/**").permitAll()
                
                // 3. 🚀 ABRIMOS TODA LA ZONA ADMIN (El controlador hará la validación manual)
                .requestMatchers("/api/admin/**").permitAll()
                
                // 4. 🚀 PERMITIMOS VER LOS ERRORES REALES (Evita el falso 403 con error: null)
                .requestMatchers("/error").permitAll()
                
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
            
        return http.build();
    }
}