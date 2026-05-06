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

    // --- PARA EL VENDEDOR (Buscador rápido) ---
    @GetMapping("/buscar")
    public ResponseEntity<List<Producto>> buscarRapido(@RequestParam String termino) {
        return ResponseEntity.ok(inventarioService.buscarRapido(termino));
    }

    @GetMapping("/codigo/{codigoBarras}")
    public ResponseEntity<Producto> buscarPorCodigo(@PathVariable String codigoBarras) {
        Producto prod = inventarioService.buscarPorCodigoBarras(codigoBarras);
        return prod != null ? ResponseEntity.ok(prod) : ResponseEntity.notFound().build();
    }

    // --- PARA GC_VENTAS (Validación interna) ---
    @GetMapping("/verificar-stock")
    public ResponseEntity<Boolean> verificarStock(@RequestParam Long productoId, @RequestParam Integer cantidad) {
        return ResponseEntity.ok(inventarioService.verificarStock(productoId, cantidad));
    }

    // --- PARA ADMINS Y BODEGUEROS (Gestión de Catálogo) ---
    @PostMapping("/productos")
    public ResponseEntity<Producto> crearProducto(@RequestBody ProductoDTO dto) {
        // Nota: La validación del token de ADMIN la hará el API Gateway en el futuro
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