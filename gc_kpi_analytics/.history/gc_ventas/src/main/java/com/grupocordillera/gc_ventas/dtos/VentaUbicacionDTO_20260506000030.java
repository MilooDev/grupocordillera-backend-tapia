package com.grupocordillera.gc_ventas.dtos;

public class VentaUbicacionDTO {
    private String region;
    private String comuna;
    private Double totalRecaudado;
    private Long cantidadVentas;

    public VentaUbicacionDTO(String region, String comuna, Double totalRecaudado, Long cantidadVentas) {
        this.region = region;
        this.comuna = comuna;
        this.totalRecaudado = totalRecaudado;
        this.cantidadVentas = cantidadVentas;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getComuna() {
        return comuna;
    }

    public void setComuna(String comuna) {
        this.comuna = comuna;
    }

    public Double getTotalRecaudado() {
        return totalRecaudado;
    }

    public void setTotalRecaudado(Double totalRecaudado) {
        this.totalRecaudado = totalRecaudado;
    }

    public Long getCantidadVentas() {
        return cantidadVentas;
    }

    public void setCantidadVentas(Long cantidadVentas) {
        this.cantidadVentas = cantidadVentas;
    }
}