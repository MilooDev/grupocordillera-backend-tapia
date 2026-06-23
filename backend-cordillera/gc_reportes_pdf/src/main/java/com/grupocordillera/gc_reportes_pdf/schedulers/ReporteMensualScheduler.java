package com.grupocordillera.gc_reportes_pdf.schedulers;

import com.grupocordillera.gc_reportes_pdf.services.ReporteOrquestadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class ReporteMensualScheduler {

    @Autowired
    private ReporteOrquestadorService reporteOrquestadorService;

    @Scheduled(cron = "0 59 23 30 * ?")
    public void ejecutarCicloMensualPDF() {
        System.out.println("⏳ [" + LocalDateTime.now() + "] INICIANDO CICLO MENSUAL AUTOMÁTICO...");
        
        try {
            // Llama al nuevo método para guardar las métricas en PostgreSQL
            LocalDate hoy = LocalDate.now();
            reporteOrquestadorService.guardarEstadisticaDiaria(hoy);
            
            System.out.println("✅ Métricas mensuales almacenadas exitosamente.");
        } catch (Exception e) {
            System.err.println("❌ Error en el ciclo automatizado: " + e.getMessage());
        }
    }
}