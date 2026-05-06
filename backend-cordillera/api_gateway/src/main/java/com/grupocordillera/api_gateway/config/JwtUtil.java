package com.grupocordillera.api_gateway.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;

@Component
public class JwtUtil {

    // La misma llave secreta que usamos en gc_auth
    private final String SECRET_KEY = "GrupoCordilleraSecretKeyParaFirmaDeTokensSeguros2026";
    private final SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    public Claims extraerReclamaciones(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean esTokenValido(String token) {
        try {
            extraerReclamaciones(token);
            return true; // Si no lanza excepción, es válido y no ha expirado
        } catch (Exception e) {
            return false; // Si expira o es falso, bloqueamos
        }
    }

    public String extraerRol(String token) {
        return extraerReclamaciones(token).get("rol", String.class);
    }
}