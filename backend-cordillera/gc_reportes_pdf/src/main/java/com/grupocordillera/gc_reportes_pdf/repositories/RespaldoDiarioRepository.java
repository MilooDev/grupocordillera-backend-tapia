package com.grupocordillera.gc_reportes_pdf.repositories;

import com.grupocordillera.gc_reportes_pdf.models.RespaldoDiario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RespaldoDiarioRepository extends JpaRepository<RespaldoDiario, Long> {
    // Retorna una lista porque ahora guardas el desglose por comuna/región para el mismo día
    List<RespaldoDiario> findByFecha(LocalDate fecha);
}