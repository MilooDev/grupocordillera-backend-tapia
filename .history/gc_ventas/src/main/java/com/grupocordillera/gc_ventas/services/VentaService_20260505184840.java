package com.grupocordillera.gc_ventas.services;

import com.grupocordillera.gc_ventas.clients.InventarioClient;
import com.grupocordillera.gc_ventas.config.RabbitMQConfig; // <-- Importación del config de Rabbit
import com.grupocordillera.gc_ventas.dtos.DetalleVentaDTO;
import com.grupocordillera.gc_ventas.dtos.VentaRequestDTO;
import com.grupocordillera.gc_ventas.dtos.VentaResponseDTO;
import com.grupocordillera.gc_ventas.models.DetalleVenta;
import com.grupocordillera.gc_ventas.models.Venta;
import com.grupocordillera.gc_ventas.repositories.VentaRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate; // <-- Importación del Template de Rabbit
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class VentaService {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private InventarioClient inventarioClient;

    // 1. INYECTAMOS EL MOTOR DE RABBITMQ
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public VentaResponseDTO procesarVenta(VentaRequestDTO requestDTO) {
        try {
            Venta nuevaVenta = new Venta();
            nuevaVenta.setClienteRut(requestDTO.getClienteRut());

            List<DetalleVenta> detallesEntidad = new ArrayList<>();
            double neto = 0;

            for (DetalleVentaDTO detalleDTO : requestDTO.getDetalles()) {
                // Llamada sincrónica a Inventario (con Circuit Breaker por debajo)
                boolean hayStock = inventarioClient.verificarStock(detalleDTO.getProductoId(),
                        detalleDTO.getCantidad());

                if (!hayStock) {
                    throw new RuntimeException("Stock insuficiente para el producto ID: " + detalleDTO.getProductoId());
                }

                // Mapeo de DTO a Entidad
                DetalleVenta detalle = new DetalleVenta();
                detalle.setProductoId(detalleDTO.getProductoId());
                detalle.setCantidad(detalleDTO.getCantidad());
                detalle.setPrecioUnitario(detalleDTO.getPrecioUnitario());

                double subtotal = detalleDTO.getCantidad() * detalleDTO.getPrecioUnitario();
                detalle.setSubtotal(subtotal);

                detallesEntidad.add(detalle);
                neto += subtotal;
            }

            nuevaVenta.setDetalles(detallesEntidad);

            // Cálculos finales
            double ivaCalculado = neto * 0.19;
            nuevaVenta.setIva(ivaCalculado);
            nuevaVenta.setTotal(neto + ivaCalculado);
            nuevaVenta.setNumeroBoleta("BOL-" + System.currentTimeMillis());

            // Guardar en Base de Datos
            Venta ventaGuardada = ventaRepository.save(nuevaVenta);

            // Devolver DTO limpio
            VentaResponseDTO responseDTO = new VentaResponseDTO(
                    ventaGuardada.getNumeroBoleta(),
                    ventaGuardada.getTotal(),
                    ventaGuardada.getIva(),
                    ventaGuardada.getFecha(),
                    "Venta registrada con éxito");

            // =========================================================
            // 2. PUBLICAR EVENTO ASÍNCRONO EN RABBITMQ
            // =========================================================
            System.out.println("[GC_VENTAS] 📢 Venta " + ventaGuardada.getNumeroBoleta()
                    + " guardada en DB. Avisando a RabbitMQ...");
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EXCHANGE_NAME,
                    RabbitMQConfig.ROUTING_KEY_VENTAS,
                    responseDTO // Enviamos el DTO de respuesta por la tubería
            );

            return responseDTO;

        } catch (Exception e) {
            throw new RuntimeException("Error al procesar la venta: " + e.getMessage());
        }
    }
}