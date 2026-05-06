package com.grupocordillera.gc_reportes_pdf.controllers;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

@RestController
@RequestMapping("/api/reportes")
public class ReportesController {

    // El frontend llamará a esta ruta, por ejemplo:
    // GET /api/reportes/descargar/Reporte_Mensual_Cordillera_2026-05-30

    @GetMapping("/descargar/{nombreArchivo}")
    public ResponseEntity<Resource> descargarPdf(@PathVariable String nombreArchivo) {
        // Le agregamos la extensión .pdf al nombre que envíe el frontend
        File file = new File(nombreArchivo + ".pdf");

        // Si el archivo no existe en el servidor, devuelve un error 404
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }

        // Si existe, lo preparamos para descargarlo físicamente
        Resource resource = new FileSystemResource(file);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }
}