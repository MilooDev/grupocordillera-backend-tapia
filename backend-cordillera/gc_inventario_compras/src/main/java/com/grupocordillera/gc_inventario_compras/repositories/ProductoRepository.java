package com.grupocordillera.gc_inventario_compras.repositories;

import com.grupocordillera.gc_inventario_compras.models.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByNombreContainingIgnoreCase(String termino);

    // 🚀 FIX: Ahora devuelve Optional para que el Service no explote
    Optional<Producto> findByCodigoBarras(String codigoBarras);

    List<Producto> findByNombreContainingIgnoreCaseOrCodigoBarrasContaining(String termino, String termino2);
}