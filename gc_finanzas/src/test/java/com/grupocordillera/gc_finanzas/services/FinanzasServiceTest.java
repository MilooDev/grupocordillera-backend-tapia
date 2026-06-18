package com.grupocordillera.gc_finanzas.services;

import com.grupocordillera.gc_finanzas.dtos.TransaccionRequestDTO;
import com.grupocordillera.gc_finanzas.dtos.TransaccionResponseDTO;
import com.grupocordillera.gc_finanzas.models.Transaccion;
import com.grupocordillera.gc_finanzas.repositories.TransaccionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FinanzasServiceTest {

    @Mock
    private TransaccionRepository transaccionRepository;

    @InjectMocks
    private FinanzasService finanzasService;

    private TransaccionRequestDTO requestMock;
    private Transaccion transaccionMock;

    @BeforeEach
    void setUp() {
        // Mockeamos el DTO de entrada para asegurar compatibilidad
        requestMock = mock(TransaccionRequestDTO.class);
        lenient().when(requestMock.getTipo()).thenReturn("INGRESO");
        lenient().when(requestMock.getMonto()).thenReturn(150000.0);
        lenient().when(requestMock.getOrigen()).thenReturn("VENTA_LOCAL");

        // Preparamos la entidad simulada que devolverá la BD
        transaccionMock = new Transaccion();
        transaccionMock.setId(1L);
        transaccionMock.setTipo("INGRESO");
        transaccionMock.setMonto(150000.0);
        transaccionMock.setOrigen("VENTA_LOCAL");
        transaccionMock.setFecha(LocalDateTime.now());
    }

    @Test
    void cuandoRegistrarMovimientoEsExitoso_entoncesRetornaResponseDTO() {
        when(transaccionRepository.save(any(Transaccion.class))).thenReturn(transaccionMock);

        TransaccionResponseDTO respuesta = finanzasService.registrarMovimiento(requestMock);

        assertNotNull(respuesta);
        assertEquals("REGISTRADO_OK", respuesta.getMensaje());
        assertEquals(1L, respuesta.getId());
        verify(transaccionRepository, times(1)).save(any(Transaccion.class));
    }

    @Test
    void cuandoRegistrarMovimientoFalla_entoncesLanzaExcepcion() {
        when(transaccionRepository.save(any(Transaccion.class))).thenThrow(new RuntimeException("Error BD"));

        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            finanzasService.registrarMovimiento(requestMock);
        });

        assertEquals("Error interno del servidor al procesar la operación financiera.", excepcion.getMessage());
    }

    @Test
    void cuandoObtenerHistorialEsExitoso_entoncesRetornaLista() {
        when(transaccionRepository.findAll()).thenReturn(Arrays.asList(transaccionMock));

        List<Transaccion> historial = finanzasService.obtenerHistorial();

        assertFalse(historial.isEmpty());
        assertEquals(1, historial.size());
        verify(transaccionRepository, times(1)).findAll();
    }

    @Test
    void cuandoObtenerHistorialFalla_entoncesLanzaExcepcion() {
        when(transaccionRepository.findAll()).thenThrow(new RuntimeException("Error BD"));

        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> {
            finanzasService.obtenerHistorial();
        });

        assertEquals("Error interno al recuperar los datos financieros.", excepcion.getMessage());
    }
}