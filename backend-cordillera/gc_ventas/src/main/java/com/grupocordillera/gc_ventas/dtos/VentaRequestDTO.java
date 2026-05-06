package com.grupocordillera.gc_ventas.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class VentaRequestDTO {
    @NotBlank(message = "El RUT del cliente es obligatorio")
    private String clienteRut;

    // NUEVOS CAMPOS
    @NotBlank(message = "La región es obligatoria")
    private String region;

    @NotBlank(message = "La comuna es obligatoria")
    private String comuna;

    @NotEmpty(message = "Debe haber al menos un detalle de venta")
    private List<DetalleVentaDTO> detalles;

    public String getClienteRut() {
        return clienteRut;
    }

    public void setClienteRut(String clienteRut) {
        this.clienteRut = clienteRut;
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

    public List<DetalleVentaDTO> getDetalles() {
        return detalles;
    }

    public void setDetalles(List<DetalleVentaDTO> detalles) {
        this.detalles = detalles;
    }
}