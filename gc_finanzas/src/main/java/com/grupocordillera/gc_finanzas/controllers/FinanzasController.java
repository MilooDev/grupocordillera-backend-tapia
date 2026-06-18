package com.grupocordillera.gc_finanzas.controllers;

import com.grupocordillera.gc_finanzas.dtos.TransaccionRequestDTO;
import com.grupocordillera.gc_finanzas.dtos.TransaccionResponseDTO;
import com.grupocordillera.gc_finanzas.models.Transaccion;
import com.grupocordillera.gc_finanzas.services.FinanzasService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/finanzas")
public class FinanzasController {

    @Autowired
    private FinanzasService finanzasService;

    @PostMapping("/registrar")
    public ResponseEntity<?> registrar(@Valid @RequestBody TransaccionRequestDTO requestDTO) {
        try {
            TransaccionResponseDTO respuesta = finanzasService.registrarMovimiento(requestDTO);
            return new ResponseEntity<>(respuesta, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/historial")
    public ResponseEntity<?> verHistorial() {
        try {
            List<Transaccion> historial = finanzasService.obtenerHistorial();
            return new ResponseEntity<>(historial, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}