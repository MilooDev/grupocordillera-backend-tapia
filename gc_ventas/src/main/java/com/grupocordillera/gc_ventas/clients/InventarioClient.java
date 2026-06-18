package com.grupocordillera.gc_ventas.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@FeignClient(name = "GC-INVENTARIO-COMPRAS")
public interface InventarioClient {

    @CircuitBreaker(name = "inventarioCB", fallbackMethod = "inventarioCaido")
    @GetMapping("/inventario/verificar/{productoId}/{cantidad}")
    boolean verificarStock(@PathVariable("productoId") Long productoId, @PathVariable("cantidad") Integer cantidad);

    // Metodo de emergencia ante una saturacion el sistema (Un metodo de contingencia)
    default boolean inventarioCaido(Long productoId, Integer cantidad, Throwable excepcion) {
        System.err.println("¡Alerta! Inventario no responde o está saturado. Activando protocolo de emergencia.");
        return false;
    }
}