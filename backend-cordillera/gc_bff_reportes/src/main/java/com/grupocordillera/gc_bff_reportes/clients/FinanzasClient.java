package com.grupocordillera.gc_bff_reportes.clients;

import com.grupocordillera.gc_bff_reportes.dto.FlujoCajaDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@FeignClient(name = "GC-FINANZAS")
public interface FinanzasClient {
    @GetMapping("/finanzas/historial")
    List<FlujoCajaDTO> obtenerHistorialFinanzas();
}