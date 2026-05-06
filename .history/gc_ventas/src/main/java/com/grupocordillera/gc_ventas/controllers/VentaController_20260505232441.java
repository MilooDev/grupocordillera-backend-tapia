package com.grupocordillera.gc_ventas.controllers;

import com.grupocordillera.gc_ventas.dtos.CierreDiarioDTO; // <-- Importación
import com.grupocordillera.gc_ventas.dtos.VentaRequestDTO;
import com.grupocordillera.gc_ventas.dtos.VentaResponseDTO;
import com.grupocordillera.gc_ventas.services.VentaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ventas")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarVenta(@Valid @RequestBody VentaRequestDTO requestDTO) {
        try {
            VentaResponseDTO respuesta = ventaService.procesarVenta(requestDTO);
            return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    // =========================================================
    // NUEVO ENDPOINT: EXPOSICIÓN DEL CIERRE DIARIO
    // =========================================================
    @GetMapping("/cierre-diario")
    public ResponseEntity<CierreDiarioDTO> obtenerCierreDelDia() {
        CierreDiarioDTO cierre = ventaService.generarCierreDiario();
        return ResponseEntity.ok(cierre);
    }
}