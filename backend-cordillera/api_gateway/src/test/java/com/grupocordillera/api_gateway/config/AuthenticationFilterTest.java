package com.grupocordillera.api_gateway.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthenticationFilter authenticationFilter;

    private GatewayFilterChain filterChain;
    private GatewayFilter filter;

    @BeforeEach
    void setUp() {
        filterChain = mock(GatewayFilterChain.class);
        filter = authenticationFilter.apply(new AuthenticationFilter.Config());
    }

    @Test
    void condicion1_cuandoEsRutaLogin_entoncesDejaPasarSinValidar() {
        MockServerHttpRequest request = MockServerHttpRequest.post("/api/auth/login").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        when(filterChain.filter(exchange)).thenReturn(Mono.empty());

        Mono<Void> result = filter.filter(exchange, filterChain);

        StepVerifier.create(result).verifyComplete();
        verify(filterChain, times(1)).filter(exchange);
        verifyNoInteractions(jwtUtil);
    }

    @Test
    void condicion2_cuandoNoHayHeaderAuthorization_entoncesResponde401() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/ventas").build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        Mono<Void> result = filter.filter(exchange, filterChain);

        StepVerifier.create(result).verifyComplete();
        assert exchange.getResponse().getStatusCode() == HttpStatus.UNAUTHORIZED;
        verifyNoInteractions(filterChain);
    }

    @Test
    void condicion3_cuandoHeaderNoEmpiezaConBearer_entoncesResponde401() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/ventas")
                .header(HttpHeaders.AUTHORIZATION, "TokenSinBearer123")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        Mono<Void> result = filter.filter(exchange, filterChain);

        StepVerifier.create(result).verifyComplete();
        assert exchange.getResponse().getStatusCode() == HttpStatus.UNAUTHORIZED;
        verifyNoInteractions(filterChain);
    }

    @Test
    void condicion4_cuandoTokenEsInvalido_entoncesResponde401() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/ventas")
                .header(HttpHeaders.AUTHORIZATION, "Bearer token.falso.invalido")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        when(jwtUtil.esTokenValido("token.falso.invalido")).thenReturn(false);

        Mono<Void> result = filter.filter(exchange, filterChain);

        StepVerifier.create(result).verifyComplete();
        assert exchange.getResponse().getStatusCode() == HttpStatus.UNAUTHORIZED;
        verifyNoInteractions(filterChain);
    }

    @Test
    void condicion5_cuandoTokenEsValido_entoncesInyectaRolYDejaPasar() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/ventas")
                .header(HttpHeaders.AUTHORIZATION, "Bearer token.super.valido")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        when(jwtUtil.esTokenValido("token.super.valido")).thenReturn(true);
        when(jwtUtil.extraerRol("token.super.valido")).thenReturn("GERENTE");
        when(filterChain.filter(any(MockServerWebExchange.class))).thenReturn(Mono.empty());

        Mono<Void> result = filter.filter(exchange, filterChain);

        StepVerifier.create(result).verifyComplete();
        
        verify(filterChain, times(1)).filter(any(MockServerWebExchange.class));
        
        String rolInyectado = exchange.getRequest().getHeaders().getFirst("X-Rol-Usuario");
        assert "GERENTE".equals(rolInyectado);
    }
}