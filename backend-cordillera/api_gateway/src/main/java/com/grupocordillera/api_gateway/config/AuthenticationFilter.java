package com.grupocordillera.api_gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    @Autowired
    JwtUtil jwtUtil;

    public AuthenticationFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {

            String path = exchange.getRequest().getURI().getPath();

            // 1. EL PASE VIP: Dejar pasar Swagger y peticiones de Login (no necesitan token)
            if (path.contains("/api/auth/login") || 
                path.contains("/v3/api-docs") || 
                path.contains("/swagger-ui") || 
                path.contains("/webjars/")) {
                return chain.filter(exchange);
            }

            // 2. Revisar si enviaron el Header de Autorización
            if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                return onError(exchange, "Acceso Denegado: Falta el token", HttpStatus.UNAUTHORIZED);
            }

            // 3. Limpiar y extraer el Token
            String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                authHeader = authHeader.substring(7);
            } else {
                return onError(exchange, "Formato de token inválido", HttpStatus.UNAUTHORIZED);
            }

            // 4. Validar el token con la firma
            if (!jwtUtil.esTokenValido(authHeader)) {
                return onError(exchange, "Token inválido o expirado. Vuelva a iniciar sesión.",
                        HttpStatus.UNAUTHORIZED);
            }

            // 5. Inyectar el rol en los headers internos para que los demás microservicios lo puedan leer
            String rol = jwtUtil.extraerRol(authHeader);
            exchange.getRequest().mutate().header("X-Rol-Usuario", rol).build();

            return chain.filter(exchange);
        };
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        exchange.getResponse().setStatusCode(httpStatus);
        return exchange.getResponse().setComplete();
    }

    public static class Config {
    }

}