package com.grupocordillera.gc_bff_reportes.clients;

import com.grupocordillera.gc_bff_reportes.dto.VentaResumenDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "GC-VENTAS")
public interface VentasClient {
    @GetMapping("/api/ventas/resumen-hoy")
    VentaResumenDTO obtenerResumenVentas();
}