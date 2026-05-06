package com.grupocordillera.gc_auth.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtil {

    // Llave secreta (Debe ser mínimo de 256 bits / 32 caracteres)
    private final String SECRET_KEY = "GrupoCordilleraSecretKeyParaFirmaDeTokensSeguros2026";

    // IMPORTANTE: En la versión moderna usamos SecretKey en lugar de Key
    private final SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    public String generarToken(String email, String rol) {
        return Jwts.builder()
                .subject(email) // Antes era setSubject
                .claim("rol", rol)
                .issuedAt(new Date(System.currentTimeMillis())) // Antes era setIssuedAt
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // Antes era setExpiration
                .signWith(key)
                .compact();
    }

    public String extraerRol(String token) {
        // SINTAXIS MODERNA (Elimina el error de parserBuilder)
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload(); // Antes era getBody()

        return claims.get("rol", String.class);
    }
}