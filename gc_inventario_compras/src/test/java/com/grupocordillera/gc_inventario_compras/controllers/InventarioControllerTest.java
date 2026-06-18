package com.grupocordillera.gc_inventario_compras.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grupocordillera.gc_inventario_compras.dtos.ProductoDTO;
import com.grupocordillera.gc_inventario_compras.models.Producto;
import com.grupocordillera.gc_inventario_compras.services.InventarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class InventarioControllerTest {

    private MockMvc mockMvc;

    @Mock
    private InventarioService inventarioService;

    @InjectMocks
    private InventarioController inventarioController;

    private ObjectMapper objectMapper;
    private ProductoDTO productoDTO;
    private Producto productoMock;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(inventarioController).build();

        productoDTO = new ProductoDTO();
        productoDTO.setNombre("Teclado");

        productoMock = new Producto();
        productoMock.setId(1L);
        productoMock.setNombre("Teclado");
    }

    @Test
    void cuandoBuscarRapido_entoncesRetorna200() throws Exception {
        when(inventarioService.buscarRapido(anyString())).thenReturn(Arrays.asList(productoMock));
        mockMvc.perform(get("/api/inventario/buscar").param("termino", "tec"))
                .andExpect(status().isOk());
    }

    @Test
    void cuandoBuscarPorCodigo_yExiste_entoncesRetorna200() throws Exception {
        when(inventarioService.buscarPorCodigoBarras("12345")).thenReturn(productoMock);
        mockMvc.perform(get("/api/inventario/codigo/12345"))
                .andExpect(status().isOk());
    }

    @Test
    void cuandoBuscarPorCodigo_yNoExiste_entoncesRetorna404() throws Exception {
        when(inventarioService.buscarPorCodigoBarras("00000")).thenReturn(null);
        mockMvc.perform(get("/api/inventario/codigo/00000"))
                .andExpect(status().isNotFound());
    }

    @Test
    void cuandoVerificarStock_entoncesRetorna200() throws Exception {
        when(inventarioService.verificarStock(1L, 5)).thenReturn(true);
        mockMvc.perform(get("/api/inventario/verificar-stock")
                .param("productoId", "1")
                .param("cantidad", "5"))
                .andExpect(status().isOk());
    }

    @Test
    void cuandoCrearProducto_entoncesRetorna201() throws Exception {
        when(inventarioService.crearProducto(any(ProductoDTO.class))).thenReturn(productoMock);
        mockMvc.perform(post("/api/inventario/productos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productoDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    void cuandoActualizarProducto_entoncesRetorna200() throws Exception {
        when(inventarioService.actualizarProducto(eq(1L), any(ProductoDTO.class))).thenReturn(productoMock);
        mockMvc.perform(put("/api/inventario/productos/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productoDTO)))
                .andExpect(status().isOk());
    }

    @Test
    void cuandoActualizarProductoFalla_entoncesRetorna400() throws Exception {
        when(inventarioService.actualizarProducto(eq(99L), any(ProductoDTO.class)))
                .thenThrow(new RuntimeException("Error"));
        mockMvc.perform(put("/api/inventario/productos/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(productoDTO)))
                .andExpect(status().isBadRequest());
    }
}