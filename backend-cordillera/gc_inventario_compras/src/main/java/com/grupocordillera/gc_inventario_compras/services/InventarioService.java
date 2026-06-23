package com.grupocordillera.gc_inventario_compras.services;

import com.grupocordillera.gc_inventario_compras.dtos.ProductoDTO;
import com.grupocordillera.gc_inventario_compras.models.Producto;
import com.grupocordillera.gc_inventario_compras.repositories.ProductoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InventarioService {

    @Autowired
    private ProductoRepository productoRepository;

    // ==========================================
    // 1. FUNCIONES PÚBLICAS (Frontend y Vendedores)
    // ==========================================
    
    // 🚀 MÉTODO CORREGIDO: Ahora sí devuelve la lista real de la base de datos
    public List<Producto> listarTodosLosProductos() {
        return productoRepository.findAll();
    }

    public List<Producto> buscarRapido(String termino) {
        return productoRepository.findByNombreContainingIgnoreCase(termino);
    }

    public Producto buscarPorCodigoBarras(String codigo) {
        return productoRepository.findByCodigoBarras(codigo);
    }

    // ==========================================
    // 2. FUNCIÓN INTERNA (Para GC_VENTAS)
    // ==========================================
    public boolean verificarStock(Long productoId, Integer cantidadRequerida) {
        Producto producto = productoRepository.findById(productoId).orElse(null);
        if (producto == null)
            return false;
        return producto.getStock() >= cantidadRequerida;
    }

    // ==========================================
    // 3. FUNCIONES DE ADMIN/BODEGUERO (CRUD)
    // ==========================================
    @Transactional
    public Producto crearProducto(ProductoDTO dto) {
        Producto nuevo = new Producto();
        mapearDtoAEntidad(dto, nuevo);
        return productoRepository.save(nuevo);
    }

    @Transactional
    public Producto actualizarProducto(Long id, ProductoDTO dto) {
        Producto existente = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        mapearDtoAEntidad(dto, existente);
        return productoRepository.save(existente);
    }

    private void mapearDtoAEntidad(ProductoDTO dto, Producto entidad) {
        entidad.setCodigoBarras(dto.getCodigoBarras());
        entidad.setNombre(dto.getNombre());
        entidad.setDescripcion(dto.getDescripcion());
        entidad.setMarca(dto.getMarca());
        entidad.setCategoria(dto.getCategoria());
        entidad.setPrecio(dto.getPrecio());
        entidad.setStock(dto.getStock());
    }
}