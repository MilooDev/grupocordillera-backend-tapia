package main.java.com.grupocordillera.gc_ventas.controllers;

import com.grupocordillera.gc_ventas.models.Venta;
import com.grupocordillera.gc_ventas.services.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ventas")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    @PostMapping("/registrar")
    public ResponseEntity<?> registrarVenta(@RequestBody Venta venta) {
        try {
            Venta nuevaVenta = ventaService.procesarVenta(venta);
            return new ResponseEntity<>(nuevaVenta, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            // Devuelve un error 400 limpio si algo falla (ej. sin stock)
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/historial")
    public ResponseEntity<List<Venta>> verHistorial() {
        return new ResponseEntity<>(ventaService.listarTodas(), HttpStatus.OK);
    }
}