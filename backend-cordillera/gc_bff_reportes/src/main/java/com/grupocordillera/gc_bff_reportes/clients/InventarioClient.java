package com.grupocordillera.gc_bff_reportes.clients;

import com.grupocordillera.gc_bff_reportes.dto.StockCriticoDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@FeignClient(name = "GC-INVENTARIO-COMPRAS")
public interface InventarioClient {
    @GetMapping("/api/inventario/stock-critico")
    List<StockCriticoDTO> obtenerStockCritico();
}