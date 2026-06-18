package com.grupocordillera.gc_auth.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grupocordillera.gc_auth.dtos.LoginRequestDTO;
import com.grupocordillera.gc_auth.services.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @Autowired
    private ObjectMapper objectMapper;

    private LoginRequestDTO loginRequestDTO;

    @BeforeEach
    void setUp() {
        loginRequestDTO = new LoginRequestDTO();
        loginRequestDTO.setEmail("admin@cordillera.cl");
        loginRequestDTO.setPassword("secreta123");
    }

    @Test
    void cuandoPeticionLoginCorrecta_entoncesRetorna200YToken() throws Exception {
        // Mockeamos la firma exacta de tu método real
        when(authService.login("admin@cordillera.cl", "secreta123")).thenReturn("mi.token.jwt");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mi.token.jwt")); // Verifica que devuelva el Map con la key "token"
    }

    @Test
    void cuandoPeticionLoginInvalida_entoncesRetorna401() throws Exception {
        // Simulamos la excepción exacta que tira tu servicio
        when(authService.login(anyString(), anyString())).thenThrow(new RuntimeException("Contraseña incorrecta"));

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestDTO)))
                .andExpect(status().isUnauthorized()) // TuHttpStatus.UNAUTHORIZED
                .andExpect(content().string("Contraseña incorrecta"));
    }
}