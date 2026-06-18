package com.grupocordillera.gc_bff_reportes.services;

import com.grupocordillera.gc_bff_reportes.clients.FinanzasClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    @Autowired
    private FinanzasClient finanzasClient;

    // Esta etiqueta guarda el resultado en Redis bajo el nombre
    // 'dashboardGerencial'
    @Cacheable(value = "dashboardGerencial", key = "'finanzas_estado'")
    public Map<String, Object> armarDashboardFinanciero() {
        // Si ves esto en la consola, significa que Redis estaba vacío y tuvo que
        // trabajar
        System.out.println("[BFF] ⚠️ Caché vacía. Consultando a los microservicios internos...");

        List<Object> historial = finanzasClient.obtenerHistorialFinanzas();

        // Aquí el BFF empaqueta todo para que React lo reciba ordenado
        Map<String, Object> respuestaFinal = new HashMap<>();
        respuestaFinal.put("modulo", "Finanzas");
        respuestaFinal.put("total_transacciones", historial.size());
        respuestaFinal.put("datos", historial);

        return respuestaFinal;
    }
}