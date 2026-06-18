package com.grupocordillera.gc_ventas.repositories;

import com.grupocordillera.gc_ventas.models.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {

    // Método para buscar ventas en un rango de fechas (Cierre de caja)
    List<Venta> findByFechaBetween(LocalDateTime inicio, LocalDateTime fin);

}