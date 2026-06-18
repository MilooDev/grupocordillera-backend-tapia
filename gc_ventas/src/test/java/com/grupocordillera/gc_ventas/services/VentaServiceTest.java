package com.grupocordillera.gc_ventas.services;

import com.grupocordillera.gc_ventas.clients.InventarioClient;
import com.grupocordillera.gc_ventas.config.RabbitMQConfig;
import com.grupocordillera.gc_ventas.dtos.DetalleVentaDTO;
import com.grupocordillera.gc_ventas.dtos.VentaRequestDTO;
import com.grupocordillera.gc_ventas.dtos.VentaResponseDTO;
import com.grupocordillera.gc_ventas.dtos.VentaUbicacionDTO;
import com.grupocordillera.gc_ventas.models.Venta;
import com.grupocordillera.gc_ventas.repositories.VentaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VentaServiceTest {

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private InventarioClient inventarioClient;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private VentaService ventaService;

    private VentaRequestDTO requestDTO;
    private DetalleVentaDTO detalleDTO;
    private Venta ventaGuardadaMock;

    @BeforeEach
    void setUp() {
        // Preparamos el DTO de entrada
        detalleDTO = new DetalleVentaDTO();
        detalleDTO.setProductoId(1L);
        detalleDTO.setCantidad(2);
        detalleDTO.setPrecioUnitario(10000.0); // Subtotal: 20000

        List<DetalleVentaDTO> detalles = new ArrayList<>();
        detalles.add(detalleDTO);

        requestDTO = new VentaRequestDTO();
        requestDTO.setClienteRut("11111111-1");
        requestDTO.setRegion("Metropolitana");
        requestDTO.setComuna("Santiago");
        requestDTO.setDetalles(detalles);

        // Preparamos lo que nos va a devolver el Repositorio al guardar
        ventaGuardadaMock = new Venta();
        ventaGuardadaMock.setNumeroBoleta("BOL-TEST123");
        ventaGuardadaMock.setTotal(23800.0); // 20000 + 19% IVA
        ventaGuardadaMock.setIva(3800.0);
        ventaGuardadaMock.setFecha(LocalDateTime.now());
    }

    @Test
    void cuandoProcesarVentaEsExitoso_entoncesGuardaYNotifica() {
        // 1. Simulamos que hay stock suficiente
        when(inventarioClient.verificarStock(1L, 2)).thenReturn(true);
        // 2. Simulamos el guardado en base de datos
        when(ventaRepository.save(any(Venta.class))).thenReturn(ventaGuardadaMock);

        // 3. Ejecutamos el método
        VentaResponseDTO respuesta = ventaService.procesarVenta(requestDTO);

        // 4. Verificaciones
        assertNotNull(respuesta);
        assertEquals("BOL-TEST123", respuesta.getNumeroBoleta());
        assertEquals(23800.0, respuesta.getTotal());
        
        // Verificamos que se guardó
        verify(ventaRepository, times(1)).save(any(Venta.class));
        
        // Verificamos que se envió el mensaje a RabbitMQ
        verify(rabbitTemplate, times(1)).convertAndSend(
                eq(RabbitMQConfig.EXCHANGE_NAME), 
                eq(RabbitMQConfig.ROUTING_KEY_VENTAS), 
                any(VentaResponseDTO.class)
        );
    }

    @Test
    void cuandoProcesarVentaSinStock_entoncesLanzaExcepcion() {
        // Simulamos que NO hay stock
        when(inventarioClient.verificarStock(1L, 2)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            ventaService.procesarVenta(requestDTO);
        });

        // Tu catch engloba la excepción con un mensaje personalizado, lo verificamos:
        assertTrue(exception.getMessage().contains("Stock insuficiente para el producto ID: 1"));

        // Verificamos que NO guardó en DB y NO mandó mensaje a RabbitMQ
        verify(ventaRepository, never()).save(any(Venta.class));
        verify(rabbitTemplate, never()).convertAndSend(anyString(), anyString(), any(Object.class));
    }

    @Test
    void cuandoGenerarCierreDiario_entoncesRetornaLista() {
        // Preparamos un mock de la respuesta del repositorio
        List<VentaUbicacionDTO> listaCierre = new ArrayList<>();
        listaCierre.add(new VentaUbicacionDTO(null, null, null, null, null)); // Ajusta según el constructor de tu DTO si es necesario

        when(ventaRepository.obtenerResumenGeografico(any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(listaCierre);

        List<VentaUbicacionDTO> resultado = ventaService.generarCierreDiario();

        assertFalse(resultado.isEmpty());
        verify(ventaRepository, times(1)).obtenerResumenGeografico(any(), any());
    }
}