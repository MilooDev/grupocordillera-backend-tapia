package com.grupocordillera.gc_reportes_pdf.clients;

import com.grupocordillera.gc_reportes_pdf.dtos.CierreDiarioDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "gc-ventas")
public interface VentasClient {

    @GetMapping("/api/ventas/interno/cierre-diario")
    CierreDiarioDTO obtenerDatosCierreDiario(@RequestParam("fecha") String fecha);
}