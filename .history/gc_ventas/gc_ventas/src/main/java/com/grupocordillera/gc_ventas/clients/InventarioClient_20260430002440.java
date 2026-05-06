package main.java.com.grupocordillera.gc_ventas.clients;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "GC-INVENTARIO-COMPRAS")
public interface InventarioClient {
    @GetMapping("/inventario/verificar/{productoId}/{cantidad}")
    boolean verificarStock(@PathVariable("productoId") Long productoId, @PathVariable("cantidad") Integer cantidad);
}