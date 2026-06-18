package main.java.com.grupocordillera.gc_inventario_compras.services;

import main.java.com.grupocordillera.gc_inventario_compras.models.Producto;
import com.grupocordillera.gc_inventario_compras.repositories.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InventarioService {

    @Autowired
    private ProductoRepository productoRepository;

    public boolean verificarDisponibilidad(Long productoId, Integer cantidadRequerida) {
        try {
            Optional<Producto> productoOpt = productoRepository.findById(productoId);

            if (productoOpt.isPresent()) {
                Producto producto = productoOpt.get();
                return producto.getStock() >= cantidadRequerida;
            }
            return false; // Si no existe el producto, devolvemos false limpiamente

        } catch (Exception e) {
            // Manejo de la excepción si la BD se desconecta o falla
            throw new RuntimeException("Error interno al acceder a la base de datos de inventario.");
        }
    }
}