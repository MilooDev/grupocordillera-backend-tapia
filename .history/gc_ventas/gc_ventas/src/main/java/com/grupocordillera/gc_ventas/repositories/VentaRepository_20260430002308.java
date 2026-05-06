package main.java.com.grupocordillera.gc_ventas.repositories;

import com.grupocordillera.gc_ventas.models.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
}