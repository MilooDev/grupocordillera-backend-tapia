package com.grupocordillera.gc_bff_reportes.dto;

public class StockCriticoDTO {
    private String codigoProducto;
    private String nombre;
    private Integer unidadesRestantes;

    public String getCodigoProducto() {
        return codigoProducto;
    }

    public void setCodigoProducto(String codigoProducto) {
        this.codigoProducto = codigoProducto;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getUnidadesRestantes() {
        return unidadesRestantes;
    }

    public void setUnidadesRestantes(Integer unidadesRestantes) {
        this.unidadesRestantes = unidadesRestantes;
    }
}