package com.grupocordillera.api_gateway.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
public class JwtUtil {

    private final String secretString = "EstaEsLaClaveSecretaSuperProtegidaDeGrupoCordillera2026_SeguridadTotal!";
    private SecretKey key;

    public JwtUtil() {
        this.key = Keys.hmacShaKeyFor(secretString.getBytes(StandardCharsets.UTF_8));
    }

    public void validateToken(String token) throws Exception {
        // Si el token falla (expirado o alterado), esto lanzará una excepción
        Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
    }
}