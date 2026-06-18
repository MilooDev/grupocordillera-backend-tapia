package com.grupocordillera.gc_finanzas.dtos;

import java.time.LocalDateTime;

public class TransaccionResponseDTO {
    
    private Long id;
    private String tipo;
    private Double monto;
    private String origen;
    private LocalDateTime fecha;
    private String mensaje;

    // Constructor vacío
    public TransaccionResponseDTO() {
    }

    // Constructor con todos los parámetros
    public TransaccionResponseDTO(Long id, String tipo, Double monto, String origen, LocalDateTime fecha, String mensaje) {
        this.id = id;
        this.tipo = tipo;
        this.monto = monto;
        this.origen = origen;
        this.fecha = fecha;
        this.mensaje = mensaje;
    }

    // --- GETTERS Y SETTERS REALES (Sin excepciones) ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}