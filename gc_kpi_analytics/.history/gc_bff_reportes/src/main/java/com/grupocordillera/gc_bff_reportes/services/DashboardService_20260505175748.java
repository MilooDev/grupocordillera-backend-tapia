package com.grupocordillera.gc_bff_reportes.services;

import com.grupocordillera.gc_bff_reportes.clients.FinanzasClient;
import com.grupocordillera.gc_bff_reportes.clients.InventarioClient;
import com.grupocordillera.gc_bff_reportes.clients.VentasClient;
import com.grupocordillera.gc_bff_reportes.dto.DashboardGlobalDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class DashboardService {

    @Autowired
    private VentasClient ventasClient;
    @Autowired
    private InventarioClient inventarioClient;
    @Autowired
    private FinanzasClient finanzasClient;

    @Cacheable(value = "dashboardCompleto", key = "'estado_global'")
    public DashboardGlobalDTO obtenerEstadoGlobalEmpresa() {
        System.out.println("[BFF] ⚠️ Caché vacía. Orquestando peticiones a los microservicios...");

        DashboardGlobalDTO reporteFinal = new DashboardGlobalDTO();

        try {
            reporteFinal.setVentas(ventasClient.obtenerResumenVentas());
            reporteFinal.setInventarioCritico(inventarioClient.obtenerStockCritico());
            reporteFinal.setFlujoCaja(finanzasClient.obtenerHistorialFinanzas());
            reporteFinal.setFechaSincronizacion(LocalDateTime.now().toString());
            reporteFinal.setEstadoRespuesta("OK");
        } catch (Exception e) {
            reporteFinal.setEstadoRespuesta("ERROR_DE_COMUNICACION: " + e.getMessage());
            System.err.println("[BFF] Error conectando con un servicio interno: " + e.getMessage());
        }

        return reporteFinal;
    }
}