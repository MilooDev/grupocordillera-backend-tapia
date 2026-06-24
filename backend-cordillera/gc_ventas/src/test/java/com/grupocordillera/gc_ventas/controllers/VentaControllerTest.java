package com.grupocordillera.gc_ventas.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grupocordillera.gc_ventas.dtos.CierreDiarioDTO;
import com.grupocordillera.gc_ventas.dtos.DetalleVentaDTO;
import com.grupocordillera.gc_ventas.dtos.VentaRequestDTO;
import com.grupocordillera.gc_ventas.dtos.VentaResponseDTO;
import com.grupocordillera.gc_ventas.services.VentasService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class VentaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private VentasService ventasService;

    @InjectMocks
    private VentaController ventaController;

    private ObjectMapper objectMapper;
    private VentaRequestDTO requestDTO;
    private VentaResponseDTO responseDTO;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(ventaController).build();

        // 1. Armamos un detalle válido para que pase el @Valid del controlador
        DetalleVentaDTO detalle = new DetalleVentaDTO();
        detalle.setProductoId(1L);
        detalle.setCantidad(2);
        detalle.setPrecioUnitario(10000.0);
        List<DetalleVentaDTO> detalles = new ArrayList<>();
        detalles.add(detalle);

        // 2. Armamos el Request completo
        requestDTO = new VentaRequestDTO();
        requestDTO.setClienteRut("11111111-1");
        requestDTO.setRegion("Metropolitana");
        requestDTO.setComuna("Santiago");
        requestDTO.setDetalles(detalles);
        
        responseDTO = new VentaResponseDTO(
                "BOL-123", 23800.0, 3800.0, LocalDateTime.now(), "Venta registrada con éxito"
        );
    }

    @Test
    void cuandoRegistrarVentaExitoso_entoncesRetorna201() throws Exception {
        when(ventasService.procesarVenta(any(VentaRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/ventas/registrar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    void cuandoRegistrarVentaFalla_entoncesRetorna400() throws Exception {
        when(ventasService.procesarVenta(any(VentaRequestDTO.class)))
                .thenThrow(new RuntimeException("Error al procesar la venta: Stock insuficiente"));

        mockMvc.perform(post("/api/ventas/registrar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Error al procesar la venta: Stock insuficiente"));
    }

    @Test
    void cuandoObtenerCierreDiario_entoncesRetorna200() throws Exception {
        CierreDiarioDTO mockCierre = new CierreDiarioDTO();
        mockCierre.setFecha(LocalDate.now());
        mockCierre.setTotalRecaudado(50000.0);

        when(ventasService.generarCierreDiario(any(LocalDate.class))).thenReturn(mockCierre);

        mockMvc.perform(get("/api/ventas/interno/cierre-diario")
                .param("fecha", LocalDate.now().toString())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }
}