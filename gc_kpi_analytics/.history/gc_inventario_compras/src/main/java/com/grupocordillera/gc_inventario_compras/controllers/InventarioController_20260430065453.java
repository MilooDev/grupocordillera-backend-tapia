package com.grupocordillera.gc_inventario_compras.controllers;

import com.grupocordillera.gc_inventario_compras.services.InventarioService;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventario")
@Validated 
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    @GetMapping("/verificar/{productoId}/{cantidad}")
    public ResponseEntity<?> verificarStock(
            @PathVariable("productoId") Long productoId,
            @PathVariable("cantidad") @Min(value = 1, message = "La cantidad a verificar debe ser al menos 1") Integer cantidad) {

        try {
            boolean hayStock = inventarioService.verificarDisponibilidad(productoId, cantidad);
            return new ResponseEntity<>(hayStock, HttpStatus.OK);
        } catch (RuntimeException e) {
            // Capturamos el error del servicio para devolver un mensaje controlado
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}