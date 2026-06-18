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

        // MAGIA SQL: Unimos Venta con Detalles para agrupar por Producto, Región y
        // Comuna
        @Query("SELECT new com.grupocordillera.gc_ventas.dtos.VentaUbicacionDTO(v.region, v.comuna, d.productoId, SUM(d.subtotal), SUM(d.cantidad)) "
                        +
                        "FROM Venta v JOIN v.detalles d WHERE v.fecha BETWEEN :inicio AND :fin GROUP BY v.region, v.comuna, d.productoId")
        List<VentaUbicacionDTO> obtenerResumenGeografico(@Param("inicio") LocalDateTime inicio,
                        @Param("fin") LocalDateTime fin);
}