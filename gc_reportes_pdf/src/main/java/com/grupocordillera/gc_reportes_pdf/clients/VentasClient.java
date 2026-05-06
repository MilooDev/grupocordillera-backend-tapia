package com.grupocordillera.gc_reportes_pdf.clients;

import com.grupocordillera.gc_reportes_pdf.dtos.VentaUbicacionDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@FeignClient(name = "GC-VENTAS")
public interface VentasClient {
    @GetMapping("/ventas/cierre-diario")
    List<VentaUbicacionDTO> obtenerCierreDelDia();
}