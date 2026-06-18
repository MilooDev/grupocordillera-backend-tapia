package com.grupocordillera.gc_ventas.dto;

import java.time.LocalDate;

public class CierreDiarioDTO {
    private LocalDate fecha;
    private Double totalRecaudado;
    private Integer cantidadVentas;

    public CierreDiarioDTO() {
    }

    public CierreDiarioDTO(LocalDate fecha, Double totalRecaudado, Integer cantidadVentas) {
        this.fecha = fecha;
        this.totalRecaudado = totalRecaudado;
        this.cantidadVentas = cantidadVentas;
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
}