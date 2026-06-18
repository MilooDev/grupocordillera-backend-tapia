package main.java.com.grupocordillera.gc_ventas.dtos;

import java.time.LocalDateTime;

public class VentaResponseDTO {
    private String numeroBoleta;
    private Double total;
    private Double iva;
    private LocalDateTime fecha;
    private String mensaje;

    public VentaResponseDTO(String numeroBoleta, Double total, Double iva, LocalDateTime fecha, String mensaje) {
        this.numeroBoleta = numeroBoleta;
        this.total = total;
        this.iva = iva;
        this.fecha = fecha;
        this.mensaje = mensaje;
    }

    // Getters
    public String getNumeroBoleta() {
        return numeroBoleta;
    }

    public Double getTotal() {
        return total;
    }

    public Double getIva() {
        return iva;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public String getMensaje() {
        return mensaje;
    }
}