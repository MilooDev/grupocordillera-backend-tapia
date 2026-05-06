package com.grupocordillera.gc_bff_reportes.controllers;

import com.grupocordillera.gc_bff_reportes.services.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dashboard")
public class BffController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/financiero")
    public ResponseEntity<?> obtenerDashboard() {
        return ResponseEntity.ok(dashboardService.armarDashboardFinanciero());
    }
}