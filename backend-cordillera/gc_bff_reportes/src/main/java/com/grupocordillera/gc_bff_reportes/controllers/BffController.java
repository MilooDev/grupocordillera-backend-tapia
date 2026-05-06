package com.grupocordillera.gc_bff_reportes.controllers;

import com.grupocordillera.gc_bff_reportes.services.DashboardService;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequestMapping("/api/v1/dashboard")
public class BffController {

    private final Bucket bucket;

    @Autowired
    private DashboardService dashboardService;

    public BffController() {
        // Limita a 10 peticiones, recarga 10 tokens por minuto (Rate Limiting)
        Bandwidth limit = Bandwidth.classic(10, Refill.intervally(10, Duration.ofMinutes(1)));
        this.bucket = Bucket.builder().addLimit(limit).build();
    }

    @GetMapping("/global")
    public ResponseEntity<?> getGlobalDashboard() {
        if (bucket.tryConsume(1)) {
            return ResponseEntity.ok(dashboardService.obtenerEstadoGlobalEmpresa());
        }
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body("Límite de peticiones excedido. Protección anti-colapso activada. Intente en un minuto.");
    }
}