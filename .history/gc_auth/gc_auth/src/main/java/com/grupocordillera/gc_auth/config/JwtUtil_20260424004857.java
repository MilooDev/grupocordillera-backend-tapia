package com.grupocordillera.gc_auth.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    // 1. LA CLAVE: Debe ser larga para el algoritmo HS256 (mínimo 64 caracteres
    // recomendados)
    private final String secretString = "EstaEsLaClaveSecretaSuperProtegidaDeGrupoCordillera2026_SeguridadTotal!";
    private SecretKey key;

    // 2. EL TIEMPO: 36,000,000 ms = 10 Horas
    private final long expirationTime = 36000000;

    @PostConstruct
    public void init() {
        // Convertimos el String en una llave real compatible con JJWT 0.12+
        this.key = Keys.hmacShaKeyFor(secretString.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String email, String rol) {
        return Jwts.builder()
                .subject(email)
                .claim("rol", rol) // Guardamos el rol para el sistema de Grupo Cordillera
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(key) // Firma automática con el algoritmo detectado
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            // Reemplazo de parserBuilder() por parser() y verifyWith()
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            // Si el token expiró o la firma no coincide, llega aquí
            return false;
        }
    }

    public String getEmailFromToken(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload() // Reemplazo de getBody() por getPayload()
                .getSubject();
    }
}