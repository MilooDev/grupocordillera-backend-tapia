package com.grupocordillera.gc_bff_reportes.services;

import com.grupocordillera.gc_bff_reportes.clients.FinanzasClient;
import com.grupocordillera.gc_bff_reportes.clients.InventarioClient;
import com.grupocordillera.gc_bff_reportes.clients.VentasClient;
import com.grupocordillera.gc_bff_reportes.dto.DashboardGlobalDTO;
import com.grupocordillera.gc_bff_reportes.dto.VentaResumenDTO; // <-- Importamos el DTO correcto
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardServiceTest {

    @Mock
    private VentasClient ventasClient;

    @Mock
    private InventarioClient inventarioClient;

    @Mock
    private FinanzasClient finanzasClient;

    @InjectMocks
    private DashboardService dashboardService;

    @BeforeEach
    void setUp() {
        // Configuraciones previas si fueran necesarias
    }

    @Test
    void cuandoObtenerEstadoGlobalEsExitoso_entoncesRetornaDTOCompleto() {
        // CORRECCIÓN: Creamos un mock del tipo exacto que espera tu cliente
        VentaResumenDTO mockVentas = mock(VentaResumenDTO.class);
        
        when(ventasClient.obtenerResumenVentas()).thenReturn(mockVentas);
        when(inventarioClient.obtenerStockCritico()).thenReturn(new ArrayList<>());
        when(finanzasClient.obtenerHistorialFinanzas()).thenReturn(new ArrayList<>());

        DashboardGlobalDTO resultado = dashboardService.obtenerEstadoGlobalEmpresa();

        assertNotNull(resultado);
        assertEquals("OK", resultado.getEstadoRespuesta());
        assertNotNull(resultado.getFechaSincronizacion());
        
        verify(ventasClient, times(1)).obtenerResumenVentas();
        verify(inventarioClient, times(1)).obtenerStockCritico();
        verify(finanzasClient, times(1)).obtenerHistorialFinanzas();
    }

    @Test
    void cuandoMicroserviciosFallan_entoncesPlanDeRespaldoRetornaEmergencia() {
        // Simulamos la excepción que lanza el Circuit Breaker
        Throwable excepcionSimulada = new RuntimeException("Timeout o error de conexión");

        DashboardGlobalDTO resultadoEmergencia = dashboardService.planDeRespaldoDashboard(excepcionSimulada);

        assertNotNull(resultadoEmergencia);
        assertTrue(resultadoEmergencia.getEstadoRespuesta().contains("MODO_DEGRADADO"));
        assertNotNull(resultadoEmergencia.getInventarioCritico()); 
        assertNotNull(resultadoEmergencia.getFlujoCaja());
    }
}