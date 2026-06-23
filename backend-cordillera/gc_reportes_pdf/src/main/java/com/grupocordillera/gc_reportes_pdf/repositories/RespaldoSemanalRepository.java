package com.grupocordillera.gc_reportes_pdf.repositories;

import com.grupocordillera.gc_reportes_pdf.models.RespaldoSemanal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RespaldoSemanalRepository extends JpaRepository<RespaldoSemanal, Long> {
    Optional<RespaldoSemanal> findBySemanaId(String semanaId);
}