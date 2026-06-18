package com.grupocordillera.gc_bff_reportes.dto;

public class FlujoCajaDTO {
    private String tipoMovimiento;
    private Double monto;

    public String getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(String tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }
}