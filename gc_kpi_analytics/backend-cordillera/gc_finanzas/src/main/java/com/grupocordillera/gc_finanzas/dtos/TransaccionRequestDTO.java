package com.grupocordillera.gc_finanzas.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class TransaccionRequestDTO {

    @NotBlank(message = "El tipo es obligatorio")
    @Pattern(regexp = "^(INGRESO|EGRESO)$", message = "Operación rechazada: Tipo de transacción no reconocido.")
    private String tipo;

    @NotNull(message = "El monto es obligatorio")
    @Min(value = 1, message = "Operación rechazada: El monto mínimo es $1.")
    @Max(value = 100000000, message = "Operación rechazada: Monto excede el límite permitido por seguridad.")
    private Double monto;

    @NotBlank(message = "El origen es obligatorio")
    // Previene ataques XSS y SQLi bloqueando símbolos como < > ' " ; =
    @Pattern(regexp = "^[a-zA-Z0-9_\\-]+$", message = "Operación rechazada: Caracteres inválidos en el origen.")
    private String origen;

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public Double getMonto() { return monto; }
    public void setMonto(Double monto) { this.monto = monto; }
    public String getOrigen() { return origen; }
    public void setOrigen(String origen) { this.origen = origen; }
}