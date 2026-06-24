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

    public List<Producto> listarTodosLosProductos() {
        return productoRepository.findAll();
    }

    public List<Producto> buscarRapido(String termino) {
        return productoRepository.findByNombreContainingIgnoreCaseOrCodigoBarrasContaining(termino, termino);
    }

    public Producto buscarPorCodigoBarras(String codigoBarras) {
        return productoRepository.findByCodigoBarras(codigoBarras).orElse(null);
    }

    public Boolean verificarStock(Long productoId, Integer cantidad) {
        return productoRepository.findById(productoId)
                .map(producto -> producto.getStock() >= cantidad)
                .orElse(false);
    }

    // 🚀 NUEVO: Método que descuenta físicamente el stock de la base de datos
    @Transactional
    public void descontarStock(Long productoId, Integer cantidad) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado en la base de datos"));
        
        if (producto.getStock() < cantidad) {
            throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre());
        }
        
        producto.setStock(producto.getStock() - cantidad);
        productoRepository.save(producto);
        System.out.println("📉 [GC_INVENTARIO] Stock actualizado. Nuevo stock de " + producto.getNombre() + ": " + producto.getStock());
    }

    @Transactional
    public Producto crearProducto(ProductoDTO dto) {
        Producto nuevo = new Producto();
        nuevo.setCodigoBarras(dto.getCodigoBarras());
        nuevo.setNombre(dto.getNombre());
        nuevo.setDescripcion(dto.getDescripcion());
        nuevo.setMarca(dto.getMarca());
        nuevo.setCategoria(dto.getCategoria());
        nuevo.setPrecio(dto.getPrecio());
        nuevo.setStock(dto.getStock());
        return productoRepository.save(nuevo);
    }

    @Transactional
    public Producto actualizarProducto(Long id, ProductoDTO dto) {
        return productoRepository.findById(id).map(producto -> {
            producto.setCodigoBarras(dto.getCodigoBarras());
            producto.setNombre(dto.getNombre());
            producto.setDescripcion(dto.getDescripcion());
            producto.setMarca(dto.getMarca());
            producto.setCategoria(dto.getCategoria());
            producto.setPrecio(dto.getPrecio());
            producto.setStock(dto.getStock());
            return productoRepository.save(producto);
        }).orElseThrow(() -> new RuntimeException("Producto no encontrado para actualizar"));
    }
}