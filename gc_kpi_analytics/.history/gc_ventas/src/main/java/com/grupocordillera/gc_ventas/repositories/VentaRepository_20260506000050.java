package com.grupocordillera.gc_ventas.repositories;

import com.grupocordillera.gc_ventas.dtos.VentaUbicacionDTO;
import com.grupocordillera.gc_ventas.models.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {

    // Agrupa las ventas por Región y Comuna usando JPQL
    @Query("SELECT new com.grupocordillera.gc_ventas.dtos.VentaUbicacionDTO(v.region, v.comuna, SUM(v.total), COUNT(v)) "
            +
            "FROM Venta v WHERE v.fecha BETWEEN :inicio AND :fin GROUP BY v.region, v.comuna")
    List<VentaUbicacionDTO> obtenerResumenGeografico(@Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin);
}