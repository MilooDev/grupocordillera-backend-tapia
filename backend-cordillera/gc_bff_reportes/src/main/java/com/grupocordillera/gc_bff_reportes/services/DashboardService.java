package com.grupocordillera.gc_bff_reportes.services;

import com.grupocordillera.gc_bff_reportes.clients.FinanzasClient;
import com.grupocordillera.gc_bff_reportes.clients.InventarioClient;
import com.grupocordillera.gc_bff_reportes.clients.VentasClient;
import com.grupocordillera.gc_bff_reportes.dto.DashboardGlobalDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    @Autowired
    private VentasClient ventasClient;
    @Autowired
    private InventarioClient inventarioClient;
    @Autowired
    private FinanzasClient finanzasClient;
    
    // 🚀 NUEVO: Herramienta para leer directamente de Mongo
    @Autowired
    private MongoTemplate mongoTemplate;

    @CircuitBreaker(name = "dashboardCB", fallbackMethod = "planDeRespaldoDashboard")
    @Cacheable(value = "dashboardCompleto", key = "'estado_global'")
    public DashboardGlobalDTO obtenerEstadoGlobalEmpresa() {
        System.out.println("[BFF] 🚀 Consultando datos analíticos...");

        DashboardGlobalDTO reporteFinal = new DashboardGlobalDTO();

        // 1. BUSCAR DATOS DE PYTHON EN MONGODB
        String fechaHoy = LocalDate.now().toString();
        Query query = new Query(Criteria.where("fecha").is(fechaHoy));
        
        // Leemos la colección "kpi_ventas_diarias" que tu script de Python alimenta
        Map resultadoMongo = mongoTemplate.findOne(query, Map.class, "kpi_ventas_diarias");

        double totalVentasHoy = 0.0;
        int cantidadBoletas = 0;

        if (resultadoMongo != null) {
            totalVentasHoy = Double.parseDouble(resultadoMongo.getOrDefault("recaudacion_total", "0").toString());
            cantidadBoletas = Integer.parseInt(resultadoMongo.getOrDefault("cantidad_ventas", "0").toString());
        }

        // 2. ARMAR LOS KPIs
        DashboardGlobalDTO.KpisDTO kpis = new DashboardGlobalDTO.KpisDTO();
        kpis.setTotalRecaudado(totalVentasHoy); // Dato real de Python
        kpis.setBoletasEmitidas(cantidadBoletas); // Dato real de Python
        kpis.setProductoEstrella("Sincronizando..."); // Pendiente de cálculo por Python
        
        try {
            // Obtenemos el stock crítico consultando a la API de Inventario
            kpis.setStockCritico(inventarioClient.obtenerStockCritico().size());
        } catch (Exception e) {
            kpis.setStockCritico(0);
        }
        reporteFinal.setKpis(kpis);

        // 3. ARMAR GRÁFICO DE LÍNEAS (Por ahora con estructura fija hasta que Python calcule la semana)
        DashboardGlobalDTO.VentasSemanaDTO ventasSemana = new DashboardGlobalDTO.VentasSemanaDTO();
        ventasSemana.setLabels(Arrays.asList("Lunes", "Martes", "Miércoles", "Jueves", "Viernes", "Sábado", "Domingo"));
        ventasSemana.setData(Arrays.asList(0.0, 0.0, 0.0, 0.0, totalVentasHoy, 0.0, 0.0));
        reporteFinal.setVentasSemana(ventasSemana);

        // 4. ARMAR GRÁFICO/TABLA DE COMUNAS
        DashboardGlobalDTO.ComunaDTO comuna1 = new DashboardGlobalDTO.ComunaDTO("Santiago Centro", totalVentasHoy, "-", "-");
        reporteFinal.setAnaliticaComunas(Arrays.asList(comuna1));

        return reporteFinal;
    }

    // ==========================================
    // 🛡️ EL PLAN B (FALLBACK METHOD)
    // ==========================================
    public DashboardGlobalDTO planDeRespaldoDashboard(Throwable e) {
        System.err.println("[BFF - CIRCUIT BREAKER] ⚠️ Base de datos o Microservicios caídos. Error: " + e.getMessage());

        DashboardGlobalDTO reporteEmergencia = new DashboardGlobalDTO();
        
        DashboardGlobalDTO.KpisDTO kpisNulos = new DashboardGlobalDTO.KpisDTO();
        kpisNulos.setTotalRecaudado(0);
        kpisNulos.setBoletasEmitidas(0);
        kpisNulos.setProductoEstrella("Sistemas Offline");
        kpisNulos.setStockCritico(0);
        
        DashboardGlobalDTO.VentasSemanaDTO semanaVacia = new DashboardGlobalDTO.VentasSemanaDTO();
        semanaVacia.setLabels(Arrays.asList("Sin Datos"));
        semanaVacia.setData(Arrays.asList(0.0));

        reporteEmergencia.setKpis(kpisNulos);
        reporteEmergencia.setVentasSemana(semanaVacia);
        reporteEmergencia.setAnaliticaComunas(Arrays.asList());

        return reporteEmergencia;
    }
}