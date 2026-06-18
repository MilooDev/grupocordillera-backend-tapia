package com.grupocordillera.gc_finanzas.controllers;

import com.grupocordillera.gc_finanzas.dtos.TransaccionRequestDTO;
import com.grupocordillera.gc_finanzas.dtos.TransaccionResponseDTO;
import com.grupocordillera.gc_finanzas.models.Transaccion;
import com.grupocordillera.gc_finanzas.services.FinanzasService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class FinanzasControllerTest {

    private MockMvc mockMvc;

    @Mock
    private FinanzasService finanzasService;

    @InjectMocks
    private FinanzasController finanzasController;

    private TransaccionResponseDTO responseDTO;
    private String jsonRequestValido;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(finanzasController).build();

        responseDTO = new TransaccionResponseDTO(
                1L, "INGRESO", 150000.0, "VENTA_LOCAL", LocalDateTime.now(), "REGISTRADO_OK"
        );

        // JSON crudo para evitar problemas de instanciación con el ObjectMapper y el @Valid
        jsonRequestValido = "{\"tipo\":\"INGRESO\", \"monto\":150000.0, \"origen\":\"VENTA_LOCAL\"}";
    }

    @Test
    void cuandoRegistrarExitoso_entoncesRetorna201() throws Exception {
        when(finanzasService.registrarMovimiento(any(TransaccionRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/finanzas/registrar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequestValido))
                .andExpect(status().isCreated());
    }

    @Test
    void cuandoRegistrarFalla_entoncesRetorna500() throws Exception {
        when(finanzasService.registrarMovimiento(any(TransaccionRequestDTO.class)))
                .thenThrow(new RuntimeException("Error interno del servidor al procesar la operación financiera."));

        mockMvc.perform(post("/finanzas/registrar")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonRequestValido))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error interno del servidor al procesar la operación financiera."));
    }

    @Test
    void cuandoVerHistorialExitoso_entoncesRetorna200() throws Exception {
        Transaccion t = new Transaccion();
        t.setId(1L);
        when(finanzasService.obtenerHistorial()).thenReturn(Arrays.asList(t));

        mockMvc.perform(get("/finanzas/historial")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void cuandoVerHistorialFalla_entoncesRetorna500() throws Exception {
        when(finanzasService.obtenerHistorial())
                .thenThrow(new RuntimeException("Error interno al recuperar los datos financieros."));

        mockMvc.perform(get("/finanzas/historial")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Error interno al recuperar los datos financieros."));
    }
}