package com.grupocordillera.gc_bff_reportes.controllers;

import com.grupocordillera.gc_bff_reportes.dto.DashboardGlobalDTO;
import com.grupocordillera.gc_bff_reportes.services.DashboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BffControllerTest {

    private MockMvc mockMvc;

    @Mock
    private DashboardService dashboardService;

    @InjectMocks
    private BffController bffController;

    @BeforeEach
    void setUp() {
        // Inicializamos el controlador aislado
        mockMvc = MockMvcBuilders.standaloneSetup(bffController).build();
    }

    @Test
    void cuandoPeticionesDentroDelLimite_entoncesRetorna200() throws Exception {
        DashboardGlobalDTO mockResponse = new DashboardGlobalDTO();
        mockResponse.setEstadoRespuesta("OK");
        when(dashboardService.obtenerEstadoGlobalEmpresa()).thenReturn(mockResponse);

        // Hacemos una petición legítima que consume 1 token
        mockMvc.perform(get("/api/v1/dashboard/global")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void cuandoExcedeElLimiteDePeticiones_entoncesRetorna429() throws Exception {
        DashboardGlobalDTO mockResponse = new DashboardGlobalDTO();
        mockResponse.setEstadoRespuesta("OK");
        when(dashboardService.obtenerEstadoGlobalEmpresa()).thenReturn(mockResponse);

        // Agotamos los 10 tokens del Bucket enviando 10 peticiones rápidas
        for (int i = 0; i < 10; i++) {
            mockMvc.perform(get("/api/v1/dashboard/global"));
        }

        // La petición #11 debe ser bloqueada por la protección anticolapso
        mockMvc.perform(get("/api/v1/dashboard/global")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isTooManyRequests())
                .andExpect(content().string("Límite de peticiones excedido. Protección anti-colapso activada. Intente en un minuto."));
    }
}