package com.grupocordillera.gc_ventas.services;

import com.grupocordillera.gc_ventas.clients.InventarioClient;
import com.grupocordillera.gc_ventas.models.DetalleVenta;
import com.grupocordillera.gc_ventas.models.Venta;
import com.grupocordillera.gc_ventas.repositories.VentaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class VentaService {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private InventarioClient inventarioClient;

    public Venta procesarVenta(Venta venta) {
        try {
            double neto = 0;

            if (venta.getDetalles() == null || venta.getDetalles().isEmpty()) {
                throw new IllegalArgumentException("La boleta no puede estar vacía.");
            }

            for (DetalleVenta detalle : venta.getDetalles()) {
                // LLAMADA SÍNCRONA A INVENTARIO
                boolean hayStock = inventarioClient.verificarStock(detalle.getProductoId(), detalle.getCantidad());

                if (!hayStock) {
                    throw new RuntimeException("Stock insuficiente para el producto ID: " + detalle.getProductoId());
                }

                double subtotal = detalle.getCantidad() * detalle.getPrecioUnitario();
                detalle.setSubtotal(subtotal);
                neto += subtotal;
            }

            double ivaCalculado = neto * 0.19;
            venta.setIva(ivaCalculado);
            venta.setTotal(neto + ivaCalculado);
            venta.setNumeroBoleta("BOL-" + System.currentTimeMillis());

            return ventaRepository.save(venta);

        } catch (Exception e) {
            // MANEJO DE EXCEPCIONES REQUERIDO POR EL TEMARIO
            throw new RuntimeException("Error al procesar la venta: " + e.getMessage());
        }
    }

    public List<Venta> listarTodas() {
        return ventaRepository.findAll();
    }
}