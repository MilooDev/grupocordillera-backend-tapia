package com.grupocordillera.gc_inventario_compras.repositories;

import com.grupocordillera.gc_inventario_compras.models.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    // MAGIA: Busca coincidencias parciales ("zapa" -> "Zapatillas")
    List<Producto> findByNombreContainingIgnoreCase(String termino);

    Producto findByCodigoBarras(String codigoBarras);
}