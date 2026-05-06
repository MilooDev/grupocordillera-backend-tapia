package com.grupocordillera.gc_finanzas.dtos;

import java.time.LocalDateTime;

public class TransaccionResponseDTO {
    private Long idTransaccion;
    private String tipo;
    private Double monto;
    private String origen;
    private LocalDateTime fecha;
    private String estado;

    public TransaccionResponseDTO(Long id, String tipo, Double monto, String origen, LocalDateTime fecha, String estado) {
        this.idTransaccion = id;
        this.tipo = tipo;
        this.monto = monto;
        this.origen = origen;
        this.fecha = fecha;
        this.estado = estado;
    }

    public Long getIdTransaccion() { return idTransaccion; }
    public String getTipo() { return tipo; }
    public Double getMonto() { return monto; }
    public String getOrigen() { return origen; }
    public LocalDateTime getFecha() { return fecha; }
    public String getEstado() { return estado; }
}