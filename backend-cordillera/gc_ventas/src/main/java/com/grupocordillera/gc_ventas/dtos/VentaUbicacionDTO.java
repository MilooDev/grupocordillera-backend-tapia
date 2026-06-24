package com.grupocordillera.gc_ventas.dtos;

public class VentaUbicacionDTO {
    private String region;
    private String comuna;
    private Long productoId;
    private Double totalRecaudado;
    private Long cantidadVentas;

    // Constructor vacío obligatorio para serialización (Jackson)
    public VentaUbicacionDTO() {
    }

    // 🚀 EL CONSTRUCTOR EXACTO QUE REQUIERE TU @Query DE JPQL
    public VentaUbicacionDTO(String region, String comuna, Long productoId, Double totalRecaudado, Long cantidadVentas) {
        this.region = region;
        this.comuna = comuna;
        this.productoId = productoId;
        this.totalRecaudado = totalRecaudado;
        this.cantidadVentas = cantidadVentas;
    }

    // --- GETTERS Y SETTERS ---

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

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
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