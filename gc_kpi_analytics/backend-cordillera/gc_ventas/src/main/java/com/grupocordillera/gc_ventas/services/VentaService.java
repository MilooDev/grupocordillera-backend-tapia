package com.grupocordillera.gc_ventas.services;

import com.grupocordillera.gc_ventas.clients.InventarioClient;
import com.grupocordillera.gc_ventas.config.RabbitMQConfig;
import com.grupocordillera.gc_ventas.dtos.DetalleVentaDTO;
import com.grupocordillera.gc_ventas.dtos.VentaRequestDTO;
import com.grupocordillera.gc_ventas.dtos.VentaResponseDTO;
import com.grupocordillera.gc_ventas.dtos.VentaUbicacionDTO;
import com.grupocordillera.gc_ventas.models.DetalleVenta;
import com.grupocordillera.gc_ventas.models.Venta;
import com.grupocordillera.gc_ventas.repositories.VentaRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class VentaService {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private InventarioClient inventarioClient;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public VentaResponseDTO procesarVenta(VentaRequestDTO requestDTO) {
        try {
            Venta nuevaVenta = new Venta();
            nuevaVenta.setClienteRut(requestDTO.getClienteRut());

            // Guardamos los nuevos datos geográficos
            nuevaVenta.setRegion(requestDTO.getRegion());
            nuevaVenta.setComuna(requestDTO.getComuna());

            List<DetalleVenta> detallesEntidad = new ArrayList<>();
            double neto = 0;

            for (DetalleVentaDTO detalleDTO : requestDTO.getDetalles()) {
                boolean hayStock = inventarioClient.verificarStock(detalleDTO.getProductoId(),
                        detalleDTO.getCantidad());
                if (!hayStock) {
                    throw new RuntimeException("Stock insuficiente para el producto ID: " + detalleDTO.getProductoId());
                }

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
            double ivaCalculado = neto * 0.19;
            nuevaVenta.setIva(ivaCalculado);
            nuevaVenta.setTotal(neto + ivaCalculado);
            nuevaVenta.setNumeroBoleta("BOL-" + System.currentTimeMillis());
            nuevaVenta.setFecha(LocalDateTime.now());

            Venta ventaGuardada = ventaRepository.save(nuevaVenta);

            VentaResponseDTO responseDTO = new VentaResponseDTO(
                    ventaGuardada.getNumeroBoleta(), ventaGuardada.getTotal(),
                    ventaGuardada.getIva(), ventaGuardada.getFecha(), "Venta registrada con éxito");

            System.out.println("[GC_VENTAS] 📢 Venta " + ventaGuardada.getNumeroBoleta() + " guardada en DB.");
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY_VENTAS, responseDTO);

            return responseDTO;
        } catch (Exception e) {
            throw new RuntimeException("Error al procesar la venta: " + e.getMessage());
        }
    }

    public List<VentaUbicacionDTO> generarCierreDiario() {
        LocalDateTime inicioDia = LocalDate.now().atStartOfDay();
        LocalDateTime finDia = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        return ventaRepository.obtenerResumenGeografico(inicioDia, finDia);
    }
}