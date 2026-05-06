package com.grupocordillera.gc_ventas.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class VentaRequestDTO {

    @NotBlank(message = "El RUT del cliente no puede estar vacío")
    private String clienteRut;

    @NotEmpty(message = "La boleta debe tener al menos un producto")
    @Valid 
    private List<DetalleVentaDTO> detalles;

    // Getters y Setters
    public String getClienteRut() {
        return clienteRut;
    }

    public void setClienteRut(String clienteRut) {
        this.clienteRut = clienteRut;
    }

    public List<DetalleVentaDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleVentaDTO> detalles) {
        this.detalles = detalles;
    }
}