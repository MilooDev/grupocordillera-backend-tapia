package com.grupocordillera.gc_reportes_pdf.services;

import com.grupocordillera.gc_reportes_pdf.clients.VentasClient;
import com.grupocordillera.gc_reportes_pdf.dtos.CierreDiarioDTO;
import com.grupocordillera.gc_reportes_pdf.dtos.VentaUbicacionDTO;
import com.grupocordillera.gc_reportes_pdf.models.RespaldoDiario;
import com.grupocordillera.gc_reportes_pdf.repositories.RespaldoDiarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReporteOrquestadorServiceTest {

    @Mock
    private VentasClient ventasClient;

    @Mock
    private RespaldoDiarioRepository respaldoDiarioRepository;

    @InjectMocks
    private ReporteOrquestadorService orquestadorService;

    private CierreDiarioDTO cierreDiarioMock;
    private LocalDate fechaPrueba;

    @BeforeEach
    void setUp() {
        fechaPrueba = LocalDate.now();

        // Creamos la lista de desgloses geográficos reales para el DTO
        VentaUbicacionDTO ubicacion1 = new VentaUbicacionDTO();
        ubicacion1.setRegion("Metropolitana");
        ubicacion1.setComuna("Pedro Aguirre Cerda");
        ubicacion1.setCantidadVentas(15L);
        ubicacion1.setTotalRecaudado(45000.0);

        VentaUbicacionDTO ubicacion2 = new VentaUbicacionDTO();
        ubicacion2.setRegion("Metropolitana");
        ubicacion2.setComuna("Santiago Centro");
        ubicacion2.setCantidadVentas(25L);
        ubicacion2.setTotalRecaudado(75000.0);

        List<VentaUbicacionDTO> ubicaciones = new ArrayList<>();
        ubicaciones.add(ubicacion1);
        ubicaciones.add(ubicacion2);

        // Instanciamos el DTO principal con los datos unificados
        cierreDiarioMock = new CierreDiarioDTO();
        cierreDiarioMock.setFecha(fechaPrueba);
        cierreDiarioMock.setCantidadVentas(40);
        cierreDiarioMock.setTotalRecaudado(120000.0);
        cierreDiarioMock.setVentasPorUbicacion(ubicaciones);
    }

    // ==========================================
    // PRUEBAS PARA: guardarEstadisticaDiaria
    // ==========================================

    @Test
    void cuandoGuardarEstadisticaDiariaEsExitoso_entoncesGuardaUnRegistroPorCadaUbicacion() {
        // Mapeamos el comportamiento del cliente Feign pasando la fecha como String
        when(ventasClient.obtenerDatosCierreDiario(fechaPrueba.toString())).thenReturn(cierreDiarioMock);
        when(respaldoDiarioRepository.save(any(RespaldoDiario.class))).thenReturn(new RespaldoDiario());

        assertDoesNotThrow(() -> orquestadorService.guardarEstadisticaDiaria(fechaPrueba));

        // Verificamos que se ejecute el save() 2 veces (una por cada ubicación del desglose)
        verify(respaldoDiarioRepository, times(2)).save(any(RespaldoDiario.class));
    }

    @Test
    void cuandoVentasClientDevuelveNull_entoncesLanzaRuntimeException() {
        when(ventasClient.obtenerDatosCierreDiario(fechaPrueba.toString())).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orquestadorService.guardarEstadisticaDiaria(fechaPrueba);
        });

        assertTrue(exception.getMessage().contains("No hay datos de ventas"));
        verify(respaldoDiarioRepository, never()).save(any());
    }

    // ==========================================
    // PRUEBAS PARA: generarPdfDiario (Al Vuelo)
    // ==========================================

    @Test
    void cuandoGenerarPdfDiarioEsExitoso_entoncesRetornaArregloDeBytesValido() {
        when(ventasClient.obtenerDatosCierreDiario(fechaPrueba.toString())).thenReturn(cierreDiarioMock);

        byte[] pdfResultado = orquestadorService.generarPdfDiario(fechaPrueba);

        assertNotNull(pdfResultado);
        assertTrue(pdfResultado.length > 0, "El PDF no debería estar vacío");
        
        // Verificamos que el PDF mantenga la firma estándar de archivos %PDF (los primeros 4 bytes)
        assertEquals(0x25, pdfResultado[0]); // '%'
        assertEquals(0x50, pdfResultado[1]); // 'P'
        assertEquals(0x44, pdfResultado[2]); // 'D'
        assertEquals(0x46, pdfResultado[3]); // 'F'
    }

    @Test
    void cuandoGenerarPdfDiarioFallaPorClientNull_entoncesLanzaException() {
        when(ventasClient.obtenerDatosCierreDiario(fechaPrueba.toString())).thenReturn(null);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orquestadorService.generarPdfDiario(fechaPrueba);
        });

        assertTrue(exception.getMessage().contains("Sin datos para generar PDF"));
    }
}