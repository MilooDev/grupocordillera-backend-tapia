package com.grupocordillera.api_gateway.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import jakarta.annotation.PostConstruct;

@Component
public class JwtUtil {

    // Extraemos la llave de las variables de entorno de Docker (con fallback local)
    @Value("${JWT_SECRET_KEY:GrupoCordilleraSecretKeyParaFirmaDeTokensSeguros2026}")
    private String secretKey;
    
    private SecretKey key;

    @PostConstruct
    public void init() {
        // Inicializamos la llave criptográfica una vez que Spring inyecta el valor
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

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