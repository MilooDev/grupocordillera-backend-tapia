package com.grupocordillera.gc_ventas.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@FeignClient(name = "GC-INVENTARIO-COMPRAS")
public interface InventarioClient {

    @CircuitBreaker(name = "inventarioCB", fallbackMethod = "inventarioCaido")
    @GetMapping("/api/inventario/verificar/{productoId}/{cantidad}")
    boolean verificarStock(@PathVariable("productoId") Long productoId, @PathVariable("cantidad") Integer cantidad);

    // 🚀 NUEVO: El puente para enviar la orden de descuento
    @CircuitBreaker(name = "inventarioCB", fallbackMethod = "inventarioCaidoDescuento")
    @PutMapping("/api/inventario/descontar/{productoId}/{cantidad}")
    void descontarStock(@PathVariable("productoId") Long productoId, @PathVariable("cantidad") Integer cantidad);

    default boolean inventarioCaido(Long productoId, Integer cantidad, Throwable excepcion) {
        System.err.println("¡Alerta! Inventario no responde para verificar stock. Motivo: " + excepcion.getMessage());
        return false;
    }

    // 🚀 NUEVO: Método de emergencia si falla el descuento
    default void inventarioCaidoDescuento(Long productoId, Integer cantidad, Throwable excepcion) {
        System.err.println("❌ ¡Error crítico! No se pudo descontar el stock del producto " + productoId + ". Motivo: " + excepcion.getMessage());
    }
}