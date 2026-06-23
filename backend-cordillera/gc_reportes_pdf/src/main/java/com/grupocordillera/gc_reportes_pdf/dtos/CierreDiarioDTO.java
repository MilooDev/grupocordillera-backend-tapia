package com.grupocordillera.gc_reportes_pdf.dtos;

import java.time.LocalDate;
import java.util.List;

public class CierreDiarioDTO {
    private LocalDate fecha;
    private Double totalRecaudado;
    private Integer cantidadVentas;
    private List<VentaUbicacionDTO> ventasPorUbicacion; // 🚀 Conexión con el desglose regional

    public CierreDiarioDTO() {
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Double getTotalRecaudado() {
        return totalRecaudado;
    }

    public void setTotalRecaudado(Double totalRecaudado) {
        this.totalRecaudado = totalRecaudado;
    }

    public Integer getCantidadVentas() {
        return cantidadVentas;
    }

    public void setCantidadVentas(Integer cantidadVentas) {
        this.cantidadVentas = cantidadVentas;
    }

    public List<VentaUbicacionDTO> getVentasPorUbicacion() {
        return ventasPorUbicacion;
    }

    public void setVentasPorUbicacion(List<VentaUbicacionDTO> ventasPorUbicacion) {
        this.ventasPorUbicacion = ventasPorUbicacion;
    }
}