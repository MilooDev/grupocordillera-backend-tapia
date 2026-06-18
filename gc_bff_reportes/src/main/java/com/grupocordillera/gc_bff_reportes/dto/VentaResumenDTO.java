package com.grupocordillera.gc_bff_reportes.dto;

public class VentaResumenDTO {
    private Double totalRecaudadoHoy;
    private Integer cantidadTransacciones;

    public Double getTotalRecaudadoHoy() {
        return totalRecaudadoHoy;
    }

    public void setTotalRecaudadoHoy(Double totalRecaudadoHoy) {
        this.totalRecaudadoHoy = totalRecaudadoHoy;
    }

    public Integer getCantidadTransacciones() {
        return cantidadTransacciones;
    }

    public void setCantidadTransacciones(Integer cantidadTransacciones) {
        this.cantidadTransacciones = cantidadTransacciones;
    }
}