package com.grupocordillera.gc_reportes_pdf.repositories;

import com.grupocordillera.gc_reportes_pdf.models.RespaldoSemanal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RespaldoSemanalRepository extends JpaRepository<RespaldoSemanal, Long> {
}