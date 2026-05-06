package com.grupocordillera.gc_finanzas.models;


import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transacciones_financieras")
public class Transaccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 10)
    private String tipo;

    @Column(nullable = false)
    private Double monto;

    @Column(nullable = false, length = 50)
    private String origen;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fecha = LocalDateTime.now();

    public Transaccion() {}


    // Getters y Setters
    public Long getId() { 
        return id; }

    public void setId(Long id) { 
        this.id = id; }

    public String getTipo() {
         return tipo; }

    public void setTipo(String tipo) { 
        this.tipo = tipo; }

    public Double getMonto() { 
        return monto; }

    public void setMonto(Double monto) { 
        this.monto = monto; }

    public String getOrigen() { 
        return origen; }
    
    public void setOrigen(String origen) { 
        this.origen = origen; }

    public LocalDateTime getFecha() { 
        return fecha; }

    public void setFecha(LocalDateTime fecha) { 
        this.fecha = fecha; }

}