package com.grupocordillera.gc_bff_reportes.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@FeignClient(name = "GC-FINANZAS")
public interface FinanzasClient {

    // Le decimos a Feign a qué ruta de Finanzas debe ir a tocar la puerta
    @GetMapping("/finanzas/historial")
    List<Object> obtenerHistorialFinanzas();
    // Usamos 'Object' como comodín porque el BFF solo transfiere los datos, no
    // necesita saber su estructura exacta.
}