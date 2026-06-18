package com.grupocordillera.api_gateway.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    
    private final String SECRET_KEY = "GrupoCordilleraSecretKeyParaFirmaDeTokensSeguros2026";
    private SecretKey key;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    @Test
    void cuandoTokenEsValido_entoncesRetornaTrue_yExtraeRolCorrectamente() {
        String tokenValido = Jwts.builder()
                .subject("usuario@cordillera.cl")
                .claim("rol", "CAJERO")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // Expira en 1 hora
                .signWith(key)
                .compact();

        boolean esValido = jwtUtil.esTokenValido(tokenValido);
        assertTrue(esValido, "El token debe ser detectado como válido");

        String rolExtraido = jwtUtil.extraerRol(tokenValido);
        assertEquals("CAJERO", rolExtraido, "Debe extraer el rol CAJERO correctamente");
    }

    @Test
    void cuandoTokenEstaExpirado_entoncesRetornaFalse() {
        String tokenExpirado = Jwts.builder()
                .subject("admin@cordillera.cl")
                .expiration(new Date(System.currentTimeMillis() - 1000 * 60 * 60)) 
                .signWith(key)
                .compact();

        boolean esValido = jwtUtil.esTokenValido(tokenExpirado);
        assertFalse(esValido, "Un token expirado debe retornar false");
    }

    @Test
    void cuandoTokenEsBasuraOMalFormado_entoncesRetornaFalse() {
        String tokenBasura = "un.token.completamente.inventado";
        
        boolean esValido = jwtUtil.esTokenValido(tokenBasura);
        assertFalse(esValido, "Un texto cualquiera debe retornar false");
    }
}