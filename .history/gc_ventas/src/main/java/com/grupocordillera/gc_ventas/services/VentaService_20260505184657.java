package com.grupocordillera.gc_ventas.services;

import com.grupocordillera.gc_ventas.config.RabbitMQConfig;
import com.grupocordillera.gc_ventas.dtos.VentaRequestDTO;
import com.grupocordillera.gc_ventas.dtos.VentaResponseDTO;
import com.grupocordillera.gc_ventas.models.Venta;
import com.grupocordillera.gc_ventas.repositories.VentaRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VentaService {

    @Autowired
    private VentaRepository ventaRepository;

    // 1. INYECTAMOS EL MOTOR DE RABBITMQ
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public VentaResponseDTO crearVenta(VentaRequestDTO request) {
        // Tu lógica actual para convertir el DTO a Entidad y guardar en BD
        Venta nuevaVenta = new Venta();
        // ... setear datos ...
        Venta ventaGuardada = ventaRepository.save(nuevaVenta);

        // Tu lógica actual para armar el DTO de respuesta
        VentaResponseDTO responseDTO = new VentaResponseDTO();
        // ... setear datos al responseDTO ...

        // =========================================================
        // 2. LO NUEVO: PUBLICAR EL EVENTO ASÍNCRONO
        // =========================================================
        System.out.println("[GC_VENTAS] 📢 Venta guardada. Avisando a RabbitMQ...");
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY_VENTAS,
                responseDTO // ¡Enviamos tu propio DTO por la tubería!
        );

        return responseDTO;
    }
}