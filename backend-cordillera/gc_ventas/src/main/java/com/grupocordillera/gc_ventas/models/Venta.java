package com.grupocordillera.gc_ventas.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Venta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String clienteRut;
    private String numeroBoleta;
    private Double total;
    private Double iva;
    private LocalDateTime fecha;

    // NUEVOS CAMPOS GEOGRÁFICOS
    private String region;
    private String comuna;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "venta_id")
    private List<DetalleVenta> detalles;

    public Venta() {
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getClienteRut() {
        return clienteRut;
    }

    public void setClienteRut(String clienteRut) {
        this.clienteRut = clienteRut;
    }

    public String getNumeroBoleta() {
        return numeroBoleta;
    }

    public void setNumeroBoleta(String numeroBoleta) {
        this.numeroBoleta = numeroBoleta;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public Double getIva() {
        return iva;
    }

    public void setIva(Double iva) {
        this.iva = iva;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
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

    public List<DetalleVenta> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleVenta> detalles) {
        this.detalles = detalles;
    }
}