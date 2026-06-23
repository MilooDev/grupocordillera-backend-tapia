package com.grupocordillera.gc_reportes_pdf.controllers;

import com.grupocordillera.gc_reportes_pdf.schedulers.ReporteMensualScheduler;
import com.grupocordillera.gc_reportes_pdf.services.ReporteOrquestadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/reportes")
public class ReportesController {

    @Autowired
    private ReporteOrquestadorService reporteOrquestadorService;

    @Autowired
    private ReporteMensualScheduler reporteMensualScheduler;

    // --- DESCARGA DIRECTA (Arma el PDF en el instante) ---
    @GetMapping("/descargar/diario/{fecha}")
    public ResponseEntity<byte[]> descargarPdfDiario(@PathVariable String fecha) {
        try {
            LocalDate fechaParseada = LocalDate.parse(fecha);
            
            // Generamos el binario del PDF al instante
            byte[] pdfBinario = reporteOrquestadorService.generarPdfDiario(fechaParseada);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Reporte_Diario_" + fecha + ".pdf\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBinario);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // --- PROTOCOLOS DE GUARDADO MANUAL Y AUTOMÁTICO ---
    @PostMapping("/generar-diario")
    public ResponseEntity<String> generarCierreManual(@RequestParam String fecha) {
        try {
            LocalDate fechaParseada = LocalDate.parse(fecha);
            // Guarda las métricas en PostgreSQL (Tus nuevos modelos)
            reporteOrquestadorService.guardarEstadisticaDiaria(fechaParseada);
            return ResponseEntity.ok("Estadísticas diarias guardadas en BD con éxito.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al guardar métricas: " + e.getMessage());
        }
    }

    @PostMapping("/emergencia/forzar-cierre-mensual")
    public ResponseEntity<Map<String, String>> detonarMetodoEmergencia() {
        Map<String, String> respuesta = new HashMap<>();
        try {
            reporteMensualScheduler.ejecutarCicloMensualPDF();
            respuesta.put("status", "SUCCESS");
            respuesta.put("mensaje", "Cierre mensual forzado con éxito.");
            return ResponseEntity.ok(respuesta);
        } catch (Exception e) {
            respuesta.put("status", "ERROR");
            respuesta.put("mensaje", "Fallo al forzar emergencia: " + e.getMessage());
            return ResponseEntity.internalServerError().body(respuesta);
        }
    }
}