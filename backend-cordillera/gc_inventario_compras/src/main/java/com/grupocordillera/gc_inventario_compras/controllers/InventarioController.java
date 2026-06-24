package com.grupocordillera.gc_inventario_compras.controllers;

import com.grupocordillera.gc_inventario_compras.dtos.ProductoDTO;
import com.grupocordillera.gc_inventario_compras.models.Producto;
import com.grupocordillera.gc_inventario_compras.services.InventarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    @GetMapping("/productos")
    public ResponseEntity<Object> listarTodos() {
        return ResponseEntity.ok(inventarioService.listarTodosLosProductos());
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Producto>> buscarRapido(@RequestParam String termino) {
        return ResponseEntity.ok(inventarioService.buscarRapido(termino));
    }

    @GetMapping("/codigo/{codigoBarras}")
    public ResponseEntity<Producto> buscarPorCodigo(@PathVariable String codigoBarras) {
        Producto prod = inventarioService.buscarPorCodigoBarras(codigoBarras);
        return prod != null ? ResponseEntity.ok(prod) : ResponseEntity.notFound().build();
    }

    @GetMapping("/verificar/{productoId}/{cantidad}")
    public ResponseEntity<Boolean> verificarStock(@PathVariable Long productoId, @PathVariable Integer cantidad) {
        return ResponseEntity.ok(inventarioService.verificarStock(productoId, cantidad));
    }

    // 🚀 NUEVO: Ruta expuesta para que Ventas envíe la orden de descuento
    @PutMapping("/descontar/{productoId}/{cantidad}")
    public ResponseEntity<Void> descontarStock(@PathVariable Long productoId, @PathVariable Integer cantidad) {
        inventarioService.descontarStock(productoId, cantidad);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/productos")
    public ResponseEntity<Producto> crearProducto(@RequestBody ProductoDTO dto) {
        Producto creado = inventarioService.crearProducto(dto);
        return new ResponseEntity<>(creado, HttpStatus.CREATED);
    }

    @PutMapping("/productos/{id}")
    public ResponseEntity<Producto> actualizarProducto(@PathVariable Long id, @RequestBody ProductoDTO dto) {
        try {
            Producto actualizado = inventarioService.actualizarProducto(id, dto);
            return ResponseEntity.ok(actualizado);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}