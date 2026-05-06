package com.grupocordillera.gc_finanzas.services;

import com.grupocordillera.gc_finanzas.dtos.TransaccionRequestDTO;
import com.grupocordillera.gc_finanzas.dtos.TransaccionResponseDTO;
import com.grupocordillera.gc_finanzas.models.Transaccion;
import com.grupocordillera.gc_finanzas.repositories.TransaccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FinanzasService {

    @Autowired
    private TransaccionRepository transaccionRepository;

    public TransaccionResponseDTO registrarMovimiento(TransaccionRequestDTO requestDTO) {
        try {
            Transaccion nuevaTransaccion = new Transaccion();
            nuevaTransaccion.setTipo(requestDTO.getTipo());
            nuevaTransaccion.setMonto(requestDTO.getMonto());
            nuevaTransaccion.setOrigen(requestDTO.getOrigen());
            
            Transaccion guardada = transaccionRepository.save(nuevaTransaccion);

            return new TransaccionResponseDTO(
                    guardada.getId(),
                    guardada.getTipo(),
                    guardada.getMonto(),
                    guardada.getOrigen(),
                    guardada.getFecha(),
                    "REGISTRADO_OK"
            );

        } catch (Exception e) {
            // Error opaco: Imprimimos en consola para el desarrollador, pero lanzamos un mensaje genérico al exterior
            System.err.println("[CRÍTICO] Error en BD Finanzas: " + e.getMessage());
            throw new RuntimeException("Error interno del servidor al procesar la operación financiera.");
        }
    }

    public List<Transaccion> obtenerHistorial() {
        try {
            return transaccionRepository.findAll();
        } catch (Exception e) {
            throw new RuntimeException("Error interno al recuperar los datos financieros.");
        }
    }
}