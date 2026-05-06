package com.grupocordillera.gc_ventas.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@FeignClient(name = "GC-INVENTARIO-COMPRAS")
public interface InventarioClient {

    // Le ponemos el fusible. Si falla, llama al método "inventarioCaido"
    @CircuitBreaker(name = "inventarioCB", fallbackMethod = "inventarioCaido")
    @GetMapping("/inventario/verificar/{productoId}/{cantidad}")
    boolean verificarStock(@PathVariable("productoId") Long productoId, @PathVariable("cantidad") Integer cantidad);

    // Método de emergencia para no saturar el sistema
    default boolean inventarioCaido(Long productoId, Integer cantidad, Throwable excepcion) {
        System.err.println("¡Alerta! Inventario no responde o está saturado. Activando protocolo de emergencia.");
        // Aquí decides tu regla de negocio:
        // ¿Rechazamos la venta por seguridad (false) o la aprobamos asumiendo que hay
        // stock (true)?
        // Para no arriesgar vender sin stock, devolvemos false rápido.
        return false;
    }
}