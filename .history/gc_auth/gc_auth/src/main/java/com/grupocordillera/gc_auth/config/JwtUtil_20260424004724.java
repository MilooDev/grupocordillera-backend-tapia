package com.grupocordillera.gc_auth.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    // 1. LA CLAVE: Debe tener al menos 32 caracteres para ser segura (HS256)
    private final String secretString = "EstaEsLaClaveSecretaSuperProtegidaDeGrupoCordillera2026!";
    private Key key;

    // 2. EL TIEMPO: 36,000,000 milisegundos = 10 Horas
    private final long expirationTime = 36000000;

    @PostConstruct
    public void init() {
        // Convertimos el String en una llave real para el algoritmo
        this.key = Keys.hmacShaKeyFor(secretString.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String email, String rol) {
        return Jwts.builder()
                .setSubject(email)
                .claim("rol", rol) // Guardamos el rol (GERENTE, CAJERO, etc)
                .setIssuedAt(new Date()) // Fecha de creación
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime)) // Fecha de muerte
                .signWith(key, SignatureAlgorithm.HS256) // Firma digital
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false; // Si el token expiró o la clave es falsa, devuelve false
        }
    }

    public String getEmailFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().getSubject();
    }
}