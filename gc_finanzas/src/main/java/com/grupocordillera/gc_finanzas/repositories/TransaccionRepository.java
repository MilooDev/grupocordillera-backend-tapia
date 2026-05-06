package com.grupocordillera.gc_finanzas.repositories;

import com.grupocordillera.gc_finanzas.models.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {
}