package com.grupocordillera.gc_bff_reportes.dto;

import java.util.List;

public class DashboardGlobalDTO {
    private VentaResumenDTO ventas;
    private List<StockCriticoDTO> inventarioCritico;
    private List<FlujoCajaDTO> flujoCaja;
    private String fechaSincronizacion;
    private String estadoRespuesta;

    public VentaResumenDTO getVentas() {
        return ventas;
    }

    public void setVentas(VentaResumenDTO ventas) {
        this.ventas = ventas;
    }

    public List<StockCriticoDTO> getInventarioCritico() {
        return inventarioCritico;
    }

    public void setInventarioCritico(List<StockCriticoDTO> inventarioCritico) {
        this.inventarioCritico = inventarioCritico;
    }

    public List<FlujoCajaDTO> getFlujoCaja() {
        return flujoCaja;
    }

    public void setFlujoCaja(List<FlujoCajaDTO> flujoCaja) {
        this.flujoCaja = flujoCaja;
    }

    public String getFechaSincronizacion() {
        return fechaSincronizacion;
    }

    public void setFechaSincronizacion(String fechaSincronizacion) {
        this.fechaSincronizacion = fechaSincronizacion;
    }

    public String getEstadoRespuesta() {
        return estadoRespuesta;
    }

    public void setEstadoRespuesta(String estadoRespuesta) {
        this.estadoRespuesta = estadoRespuesta;
    }
}