package com.grupocordillera.gc_reportes_pdf.controllers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.File;
import java.io.IOException;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ReportesControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private ReportesController reportesController;

    private final String NOMBRE_ARCHIVO_PRUEBA = "Reporte_Test_Virtual";

    @BeforeEach
    void setUp() throws IOException {
        mockMvc = MockMvcBuilders.standaloneSetup(reportesController).build();
        // Creamos un archivo falso para que el controlador lo encuentre
        new File(NOMBRE_ARCHIVO_PRUEBA + ".pdf").createNewFile();
    }

    @AfterEach
    void tearDown() {
        // Borramos el archivo falso después del test
        new File(NOMBRE_ARCHIVO_PRUEBA + ".pdf").delete();
    }

    @Test
    void cuandoDescargarPdfExiste_entoncesRetorna200YArchivo() throws Exception {
        mockMvc.perform(get("/api/reportes/descargar/" + NOMBRE_ARCHIVO_PRUEBA))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"" + NOMBRE_ARCHIVO_PRUEBA + ".pdf\""));
    }

    @Test
    void cuandoDescargarPdfNoExiste_entoncesRetorna404() throws Exception {
        mockMvc.perform(get("/api/reportes/descargar/ArchivoFantasmaQueNoExiste"))
                .andExpect(status().isNotFound());
    }
}