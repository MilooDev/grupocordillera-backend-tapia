package com.grupocordillera.gc_ventas.services;

import com.grupocordillera.gc_ventas.clients.InventarioClient;
import com.grupocordillera.gc_ventas.config.RabbitMQConfig;
import com.grupocordillera.gc_ventas.dtos.CierreDiarioDTO;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class VentasService {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private InventarioClient inventarioClient;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Transactional
    public VentaResponseDTO procesarVenta(VentaRequestDTO requestDTO) {
        try {
            Venta nuevaVenta = new Venta();
            nuevaVenta.setClienteRut(requestDTO.getClienteRut());
            nuevaVenta.setRegion(requestDTO.getRegion());
            nuevaVenta.setComuna(requestDTO.getComuna());

            List<DetalleVenta> detallesEntidad = new ArrayList<>();
            double neto = 0;

            for (DetalleVentaDTO detalleDTO : requestDTO.getDetalles()) {
                boolean hayStock = inventarioClient.verificarStock(detalleDTO.getProductoId(), detalleDTO.getCantidad());
                if (!hayStock) {
                    throw new RuntimeException("Stock insuficiente para el producto ID: " + detalleDTO.getProductoId());
                }

                // 🚀 AQUÍ OCURRE LA MAGIA: Le avisamos al Inventario que descuente el stock
                inventarioClient.descontarStock(detalleDTO.getProductoId(), detalleDTO.getCantidad());

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

            System.out.println("[GC_VENTAS] 📢 Venta " + ventaGuardada.getNumeroBoleta() + " guardada en DB y stock descontado.");
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY_VENTAS, responseDTO);

            return responseDTO;
        } catch (Exception e) {
            throw new RuntimeException("Error al procesar la venta: " + e.getMessage());
        }
    }

    public CierreDiarioDTO generarCierreDiario(LocalDate fecha) {
        LocalDateTime inicioDia = fecha.atStartOfDay();
        LocalDateTime finDia = fecha.atTime(LocalTime.MAX);
        
        List<VentaUbicacionDTO> desglose = ventaRepository.obtenerResumenGeografico(inicioDia, finDia);
        
        Double totalRecaudado = 0.0;
        Integer cantidadVentas = 0;
        
        for (VentaUbicacionDTO ubi : desglose) {
            totalRecaudado += (ubi.getTotalRecaudado() != null ? ubi.getTotalRecaudado() : 0.0);
            cantidadVentas += (ubi.getCantidadVentas() != null ? ubi.getCantidadVentas().intValue() : 0);
        }

        CierreDiarioDTO cierre = new CierreDiarioDTO();
        cierre.setFecha(fecha);
        cierre.setTotalRecaudado(totalRecaudado);
        cierre.setCantidadVentas(cantidadVentas);
        cierre.setVentasPorUbicacion(desglose);
        
        return cierre;
    }
}