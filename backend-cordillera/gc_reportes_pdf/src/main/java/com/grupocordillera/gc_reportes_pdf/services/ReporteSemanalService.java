package com.grupocordillera.gc_reportes_pdf.services;

import com.grupocordillera.gc_reportes_pdf.models.RespaldoSemanal;
import com.grupocordillera.gc_reportes_pdf.repositories.RespaldoSemanalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ReporteSemanalService {

    @Autowired
    private RespaldoSemanalRepository respaldoSemanalRepository;

    public RespaldoSemanal generarReporteSemanal(LocalDate inicio, LocalDate fin) {
        // Aquí irá la lógica análoga para consolidar 7 días de ventas
        // Por ahora lanzamos una excepción controlada si se intenta usar
        throw new UnsupportedOperationException("Lógica de consolidación semanal pendiente de implementación con gc_ventas");
    }
}