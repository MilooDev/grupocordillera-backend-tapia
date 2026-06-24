package com.grupocordillera.gc_ventas.controllers;

import com.grupocordillera.gc_ventas.dtos.CierreDiarioDTO;
import com.grupocordillera.gc_ventas.dtos.VentaRequestDTO;
import com.grupocordillera.gc_ventas.dtos.VentaResponseDTO;
import com.grupocordillera.gc_ventas.services.VentasService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    @Autowired
    private VentasService ventasService; // <-- Inyección corregida

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarVenta(@Valid @RequestBody VentaRequestDTO requestDTO) {
        try {
            VentaResponseDTO respuesta = ventasService.procesarVenta(requestDTO);
            return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/interno/cierre-diario")
    public ResponseEntity<CierreDiarioDTO> obtenerCierreDelDia(@RequestParam("fecha") String fecha) {
        LocalDate fechaParseada = LocalDate.parse(fecha);
        return ResponseEntity.ok(ventasService.generarCierreDiario(fechaParseada));
    }
}