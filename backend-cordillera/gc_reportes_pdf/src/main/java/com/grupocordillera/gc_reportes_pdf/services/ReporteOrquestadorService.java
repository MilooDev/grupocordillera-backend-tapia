package com.grupocordillera.gc_reportes_pdf.services;

import com.grupocordillera.gc_reportes_pdf.clients.VentasClient;
import com.grupocordillera.gc_reportes_pdf.dtos.CierreDiarioDTO;
import com.grupocordillera.gc_reportes_pdf.models.RespaldoDiario;
import com.grupocordillera.gc_reportes_pdf.repositories.RespaldoDiarioRepository;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;

@Service
public class ReporteOrquestadorService {

    @Autowired
    private VentasClient ventasClient;

    @Autowired
    private RespaldoDiarioRepository respaldoDiarioRepository;

    // 1. GUARDA TUS NUEVOS MODELOS EN LA BASE DE DATOS
    public void guardarEstadisticaDiaria(LocalDate fecha) {
        CierreDiarioDTO datos = ventasClient.obtenerDatosCierreDiario(fecha.toString());
        
        if (datos == null || datos.getTotalRecaudado() == null) {
            throw new RuntimeException("No hay datos de ventas para la fecha: " + fecha);
        }

        // Si hay desglose, guardamos un registro por ubicación según tu nuevo modelo
        if (datos.getVentasPorUbicacion() != null && !datos.getVentasPorUbicacion().isEmpty()) {
            datos.getVentasPorUbicacion().forEach(ubi -> {
                RespaldoDiario registro = new RespaldoDiario();
                registro.setFecha(fecha);
                registro.setRegion(ubi.getRegion());
                registro.setComuna(ubi.getComuna());
                // Ajustamos el tipo de Long a Integer según tu DTO
                registro.setCantidadVentas(ubi.getCantidadVentas() != null ? ubi.getCantidadVentas().intValue() : 0);
                registro.setTotalRecaudado(ubi.getTotalRecaudado());
                respaldoDiarioRepository.save(registro);
            });
        }
    }

    // 2. GENERA EL PDF "AL VUELO" PARA DESCARGAR
    public byte[] generarPdfDiario(LocalDate fecha) {
        try {
            CierreDiarioDTO datos = ventasClient.obtenerDatosCierreDiario(fecha.toString());
            if (datos == null) throw new RuntimeException("Sin datos para generar PDF");

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            
            document.open();
            Font fontTitulo = new Font(Font.HELVETICA, 18, Font.BOLD);
            Font fontCuerpo = new Font(Font.HELVETICA, 12, Font.NORMAL);

            document.add(new Paragraph("GRUPO CORDILLERA - REPORTE DE CIERRE", fontTitulo));
            document.add(new Paragraph("Fecha de Operación: " + fecha, fontCuerpo));
            document.add(new Paragraph("Cantidad Transacciones: " + datos.getCantidadVentas(), fontCuerpo));
            document.add(new Paragraph("Monto Total Recaudado: $" + datos.getTotalRecaudado(), fontCuerpo));
            document.add(new Paragraph(" "));
            
            document.add(new Paragraph("Desglose Regional:", new Font(Font.HELVETICA, 14, Font.BOLD)));
            if (datos.getVentasPorUbicacion() != null) {
                datos.getVentasPorUbicacion().forEach(ubi -> {
                    document.add(new Paragraph("- " + ubi.getRegion() + ", " + ubi.getComuna() 
                            + " | Ventas: " + ubi.getCantidadVentas() 
                            + " | Recaudado: $" + ubi.getTotalRecaudado(), fontCuerpo));
                });
            }
            
            document.close();
            return out.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Error al armar el PDF: " + e.getMessage());
        }
    }
}