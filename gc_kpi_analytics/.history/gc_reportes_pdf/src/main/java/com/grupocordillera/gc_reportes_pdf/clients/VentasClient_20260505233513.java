package com.grupocordillera.gc_reportes_pdf.clients;

import com.grupocordillera.gc_reportes_pdf.dtos.CierreDiarioDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "GC-VENTAS")
public interface VentasClient {
    @GetMapping("/ventas/cierre-diario")
    CierreDiarioDTO obtenerCierreDelDia();
}