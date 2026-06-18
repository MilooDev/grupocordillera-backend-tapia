package com.grupocordillera.gc_inventario_compras.services;

import com.grupocordillera.gc_inventario_compras.dtos.ProductoDTO;
import com.grupocordillera.gc_inventario_compras.models.Producto;
import com.grupocordillera.gc_inventario_compras.repositories.ProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventarioServiceTest {

    @Mock
    private ProductoRepository productoRepository;

    @InjectMocks
    private InventarioService inventarioService;

    private Producto productoMock;
    private ProductoDTO dtoMock;

    @BeforeEach
    void setUp() {
        productoMock = new Producto();
        productoMock.setId(1L);
        productoMock.setNombre("Teclado");
        productoMock.setStock(10);
        productoMock.setCodigoBarras("123456");

        dtoMock = new ProductoDTO();
        dtoMock.setNombre("Teclado Modificado");
        dtoMock.setStock(15);
        dtoMock.setCodigoBarras("123456");
    }

    @Test
    void cuandoBuscarRapido_entoncesRetornaLista() {
        when(productoRepository.findByNombreContainingIgnoreCase("tec")).thenReturn(Arrays.asList(productoMock));
        List<Producto> resultado = inventarioService.buscarRapido("tec");
        assertFalse(resultado.isEmpty());
        verify(productoRepository).findByNombreContainingIgnoreCase("tec");
    }

    @Test
    void cuandoBuscarPorCodigo_entoncesRetornaProducto() {
        when(productoRepository.findByCodigoBarras("123456")).thenReturn(productoMock);
        Producto encontrado = inventarioService.buscarPorCodigoBarras("123456");
        assertNotNull(encontrado);
    }

    @Test
    void cuandoVerificarStock_yEsSuficiente_entoncesRetornaTrue() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoMock));
        boolean hayStock = inventarioService.verificarStock(1L, 5); // Pide 5, hay 10
        assertTrue(hayStock);
    }

    @Test
    void cuandoVerificarStock_yEsInsuficiente_entoncesRetornaFalse() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoMock));
        boolean hayStock = inventarioService.verificarStock(1L, 15); // Pide 15, hay 10
        assertFalse(hayStock);
    }

    @Test
    void cuandoVerificarStock_yNoExisteProducto_entoncesRetornaFalse() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());
        boolean hayStock = inventarioService.verificarStock(99L, 1);
        assertFalse(hayStock);
    }

    @Test
    void cuandoCrearProducto_entoncesLoGuarda() {
        when(productoRepository.save(any(Producto.class))).thenReturn(productoMock);
        Producto guardado = inventarioService.crearProducto(dtoMock);
        assertNotNull(guardado);
        verify(productoRepository).save(any(Producto.class));
    }

    @Test
    void cuandoActualizarProducto_entoncesLoModifica() {
        when(productoRepository.findById(1L)).thenReturn(Optional.of(productoMock));
        when(productoRepository.save(any(Producto.class))).thenReturn(productoMock);
        
        Producto actualizado = inventarioService.actualizarProducto(1L, dtoMock);
        assertNotNull(actualizado);
        verify(productoRepository).save(productoMock);
    }

    @Test
    void cuandoActualizarProductoNoExistente_entoncesLanzaExcepcion() {
        when(productoRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> inventarioService.actualizarProducto(99L, dtoMock));
    }
}