package com.grupocordillera.gc_inventario_compras.repositories;

import com.grupocordillera.gc_inventario_compras.models.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
}