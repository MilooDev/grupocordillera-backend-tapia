package com.grupocordillera.gc_bff_reportes.dto;

import java.util.List;

public class DashboardGlobalDTO {

    private KpisDTO kpis;
    private VentasSemanaDTO ventasSemana;
    private List<ComunaDTO> analiticaComunas;

    public DashboardGlobalDTO() {}

    public KpisDTO getKpis() { return kpis; }
    public void setKpis(KpisDTO kpis) { this.kpis = kpis; }

    public VentasSemanaDTO getVentasSemana() { return ventasSemana; }
    public void setVentasSemana(VentasSemanaDTO ventasSemana) { this.ventasSemana = ventasSemana; }

    public List<ComunaDTO> getAnaliticaComunas() { return analiticaComunas; }
    public void setAnaliticaComunas(List<ComunaDTO> analiticaComunas) { this.analiticaComunas = analiticaComunas; }

    // ==========================================
    // CLASES INTERNAS PARA ESTRUCTURAR EL JSON
    // ==========================================
    public static class KpisDTO {
        private double totalRecaudado;
        private int boletasEmitidas;
        private String productoEstrella;
        private int stockCritico;

        // Getters y Setters
        public double getTotalRecaudado() { return totalRecaudado; }
        public void setTotalRecaudado(double totalRecaudado) { this.totalRecaudado = totalRecaudado; }
        public int getBoletasEmitidas() { return boletasEmitidas; }
        public void setBoletasEmitidas(int boletasEmitidas) { this.boletasEmitidas = boletasEmitidas; }
        public String getProductoEstrella() { return productoEstrella; }
        public void setProductoEstrella(String productoEstrella) { this.productoEstrella = productoEstrella; }
        public int getStockCritico() { return stockCritico; }
        public void setStockCritico(int stockCritico) { this.stockCritico = stockCritico; }
    }

    public static class VentasSemanaDTO {
        private List<String> labels;
        private List<Double> data;

        // Getters y Setters
        public List<String> getLabels() { return labels; }
        public void setLabels(List<String> labels) { this.labels = labels; }
        public List<Double> getData() { return data; }
        public void setData(List<Double> data) { this.data = data; }
    }

    public static class ComunaDTO {
        private String nombre;
        private double total;
        private String masVendido;
        private String menosVendido;

        public ComunaDTO(String nombre, double total, String masVendido, String menosVendido) {
            this.nombre = nombre;
            this.total = total;
            this.masVendido = masVendido;
            this.menosVendido = menosVendido;
        }

        // Getters y Setters
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
        public double getTotal() { return total; }
        public void setTotal(double total) { this.total = total; }
        public String getMasVendido() { return masVendido; }
        public void setMasVendido(String masVendido) { this.masVendido = masVendido; }
        public String getMenosVendido() { return menosVendido; }
        public void setMenosVendido(String menosVendido) { this.menosVendido = menosVendido; }
    }
}