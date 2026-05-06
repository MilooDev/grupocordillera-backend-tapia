package com.grupocordillera.gc_reportes_pdf.models;

import jakarta.persistence.*;

@Entity
public class RespaldoSemanal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String semanaId;
    private Double totalRecaudado;
    private Integer cantidadVentas;

    public RespaldoSemanal() {
    }

    // --- GETTERS Y SETTERS COMPLETOS ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSemanaId() {
        return semanaId;
    }

    // AQUÍ ESTÁ EL MÉTODO QUE TE DABA ROJO
    public void setSemanaId(String semanaId) {
        this.semanaId = semanaId;
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