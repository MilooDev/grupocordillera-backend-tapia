package com.grupocordillera.gc_reportes_pdf.services;

import com.grupocordillera.gc_reportes_pdf.clients.VentasClient;
import com.grupocordillera.gc_reportes_pdf.dtos.VentaUbicacionDTO;
import com.grupocordillera.gc_reportes_pdf.models.RespaldoDiario;
import com.grupocordillera.gc_reportes_pdf.models.RespaldoSemanal;
import com.grupocordillera.gc_reportes_pdf.repositories.RespaldoDiarioRepository;
import com.grupocordillera.gc_reportes_pdf.repositories.RespaldoSemanalRepository;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReporteOrquestadorService {

    @Autowired
    private VentasClient ventasClient;

    @Autowired
    private RespaldoDiarioRepository diarioRepo;

    @Autowired
    private RespaldoSemanalRepository semanalRepo;

    // 1. EXTRAE EL CIERRE DIARIO GEOGRÁFICO A LAS 23:50
    @Scheduled(cron = "0 50 23 * * *")
    public void recolectarCierreDiario() {
        System.out.println("[REPORTES] 📥 Solicitando cierre geográfico a GC-VENTAS...");
        try {
            List<VentaUbicacionDTO> cierresHoy = ventasClient.obtenerCierreDelDia();

            for (VentaUbicacionDTO ubicacion : cierresHoy) {
                RespaldoDiario hoy = new RespaldoDiario();
                hoy.setFecha(LocalDate.now());
                hoy.setRegion(ubicacion.getRegion());
                hoy.setComuna(ubicacion.getComuna());
                hoy.setProductoId(ubicacion.getProductoId());
                hoy.setTotalRecaudado(ubicacion.getTotalRecaudado());
                hoy.setCantidadVentas(
                        ubicacion.getCantidadVentas() != null ? ubicacion.getCantidadVentas().intValue() : 0);

                diarioRepo.save(hoy);
            }
            System.out.println("[REPORTES] ✅ Respaldo del día guardado con éxito por Producto y Comuna.");
        } catch (Exception e) {
            // ==========================================
            // PROTOCOLO DE EMERGENCIA ACTIVADO
            // ==========================================
            System.err.println("🚨 [EMERGENCIA] Conexión fallida con Ventas o BD principal caída.");
            System.err.println("🚨 Generando PDF de respaldo de emergencia con los datos salvados hasta ahora...");
            generarCierreEmergencia();
        }
    }

    // 2. GENERA REPORTE SEMANAL Y LIMPIA (DOMINGOS 23:55)
    @Transactional
    @Scheduled(cron = "0 55 23 * * SUN")
    public void generarCierreSemanal() {
        System.out.println("[REPORTES] 🔄 Generando respaldo Semanal...");
        List<RespaldoDiario> dias = diarioRepo.findAll();
        if (dias.isEmpty())
            return;

        double total = dias.stream().mapToDouble(RespaldoDiario::getTotalRecaudado).sum();
        int ventas = dias.stream().mapToInt(RespaldoDiario::getCantidadVentas).sum();

        crearPdf("Reporte_Semanal_" + LocalDate.now() + ".pdf", "Resumen Semanal de Ventas (Nacional)", total, ventas);

        RespaldoSemanal acumulado = new RespaldoSemanal();
        acumulado.setSemanaId("Semana-" + LocalDate.now());
        acumulado.setTotalRecaudado(total);
        acumulado.setCantidadVentas(ventas);
        semanalRepo.save(acumulado);

        diarioRepo.deleteAll();
    }

    // 3. GENERA REPORTE MENSUAL (ÚLTIMO DÍA DEL MES A LAS 23:59) - ¡Corregido el
    // bug de febrero!
    @Transactional
    @Scheduled(cron = "0 59 23 L * ?")
    public void generarCierreMensual() {
        System.out.println("[REPORTES] 🏆 Generando respaldo MENSUAL...");
        List<RespaldoSemanal> semanas = semanalRepo.findAll();
        if (semanas.isEmpty())
            return;

        double total = semanas.stream().mapToDouble(RespaldoSemanal::getTotalRecaudado).sum();
        int ventas = semanas.stream().mapToInt(RespaldoSemanal::getCantidadVentas).sum();

        crearPdf("Reporte_Mensual_Cordillera_" + LocalDate.now() + ".pdf", "Resumen MENSUAL (Nacional)", total, ventas);

        semanalRepo.deleteAll();
    }

    // MÉTODO DE EMERGENCIA
    @Transactional
    public void generarCierreEmergencia() {
        List<RespaldoDiario> diasSalvados = diarioRepo.findAll();
        if (diasSalvados.isEmpty())
            return;

        double total = diasSalvados.stream().mapToDouble(RespaldoDiario::getTotalRecaudado).sum();
        int ventas = diasSalvados.stream().mapToInt(RespaldoDiario::getCantidadVentas).sum();

        crearPdf(
                "Reporte_EMERGENCIA_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm"))
                        + ".pdf",
                "🚨 REPORTE DE EMERGENCIA (Fallo de Sistema)", total, ventas);
    }

    private void crearPdf(String nombreArchivo, String titulo, double total, int ventas) {
        try (Document document = new Document()) {
            PdfWriter.getInstance(document, new FileOutputStream(nombreArchivo));
            document.open();
            document.add(new Paragraph(titulo));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Total Recaudado: $" + total));
            document.add(new Paragraph("Cantidad de Transacciones: " + ventas));
            document.add(new Paragraph("Generado el: " + LocalDateTime.now()));
            document.close();
            System.out.println("[REPORTES] 📄 Archivo físico creado: " + nombreArchivo);
        } catch (Exception e) {
            System.err.println("[REPORTES] Error creando PDF: " + e.getMessage());
        }
    }
}