package com.grupocordillera.gc_bff_reportes.services;

import com.grupocordillera.gc_bff_reportes.clients.FinanzasClient;
import com.grupocordillera.gc_bff_reportes.clients.InventarioClient;
import com.grupocordillera.gc_bff_reportes.clients.VentasClient;
import com.grupocordillera.gc_bff_reportes.dto.DashboardGlobalDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
public class DashboardService {

    @Autowired
    private VentasClient ventasClient;
    @Autowired
    private InventarioClient inventarioClient;
    @Autowired
    private FinanzasClient finanzasClient;

    // Aquí le decimos que use el interruptor que configuramos en el YML
    @CircuitBreaker(name = "dashboardCB", fallbackMethod = "planDeRespaldoDashboard")
    @Cacheable(value = "dashboardCompleto", key = "'estado_global'")
    public DashboardGlobalDTO obtenerEstadoGlobalEmpresa() {
        System.out.println("[BFF] 🚀 Consultando a los microservicios...");

        DashboardGlobalDTO reporteFinal = new DashboardGlobalDTO();

        // Ya no necesitamos el try-catch, si esto falla, salta directo al Plan B
        reporteFinal.setVentas(ventasClient.obtenerResumenVentas());
        reporteFinal.setInventarioCritico(inventarioClient.obtenerStockCritico());
        reporteFinal.setFlujoCaja(finanzasClient.obtenerHistorialFinanzas());
        reporteFinal.setFechaSincronizacion(LocalDateTime.now().toString());
        reporteFinal.setEstadoRespuesta("OK");

        return reporteFinal;
    }

    // ==========================================
    // 🛡️ EL PLAN B (FALLBACK METHOD)
    // ==========================================
    // Este método DEBE tener el mismo retorno y recibir un Throwable
    public DashboardGlobalDTO planDeRespaldoDashboard(Throwable e) {
        System.err.println("[BFF - CIRCUIT BREAKER] ⚠️ Microservicios caídos. Entregando datos de emergencia. Error: "
                + e.getMessage());

        DashboardGlobalDTO reporteEmergencia = new DashboardGlobalDTO();
        reporteEmergencia.setEstadoRespuesta("MODO_DEGRADADO: Sistemas internos no disponibles temporalmente.");
        reporteEmergencia.setFechaSincronizacion(LocalDateTime.now().toString());

        // Entregamos listas vacías o DTOs nulos para que React no explote
        reporteEmergencia.setInventarioCritico(new ArrayList<>());
        reporteEmergencia.setFlujoCaja(new ArrayList<>());

        return reporteEmergencia;
    }
}