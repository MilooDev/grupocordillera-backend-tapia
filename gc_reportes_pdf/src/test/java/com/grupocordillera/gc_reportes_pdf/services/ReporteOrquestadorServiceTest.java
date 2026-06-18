package com.grupocordillera.gc_reportes_pdf.services;

import com.grupocordillera.gc_reportes_pdf.clients.VentasClient;
import com.grupocordillera.gc_reportes_pdf.dtos.VentaUbicacionDTO;
import com.grupocordillera.gc_reportes_pdf.models.RespaldoDiario;
import com.grupocordillera.gc_reportes_pdf.models.RespaldoSemanal;
import com.grupocordillera.gc_reportes_pdf.repositories.RespaldoDiarioRepository;
import com.grupocordillera.gc_reportes_pdf.repositories.RespaldoSemanalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReporteOrquestadorServiceTest {

    @Mock
    private VentasClient ventasClient;

    @Mock
    private RespaldoDiarioRepository diarioRepo;

    @Mock
    private RespaldoSemanalRepository semanalRepo;

    @InjectMocks
    private ReporteOrquestadorService orquestadorService;

    private List<VentaUbicacionDTO> listaUbicacionesMock;
    private List<RespaldoDiario> listaDiariaMock;
    private List<RespaldoSemanal> listaSemanalMock;

    @BeforeEach
    void setUp() {
        // En lugar de instanciarlo y usar setters, mockeamos el DTO para evitar errores de compilación
        VentaUbicacionDTO dto = mock(VentaUbicacionDTO.class);
        
        // Usamos lenient() por si algún test no ocupa todos los campos
        lenient().when(dto.getRegion()).thenReturn("Metropolitana");
        lenient().when(dto.getComuna()).thenReturn("Santiago");
        lenient().when(dto.getTotalRecaudado()).thenReturn(50000.0);
        lenient().when(dto.getProductoId()).thenReturn(1L);
        lenient().when(dto.getCantidadVentas()).thenReturn(5L); // Ajusta a 5 si tu DTO retorna Integer en lugar de Long

        listaUbicacionesMock = new ArrayList<>();
        listaUbicacionesMock.add(dto);

        RespaldoDiario diario = new RespaldoDiario();
        diario.setTotalRecaudado(10000.0);
        diario.setCantidadVentas(2);
        listaDiariaMock = new ArrayList<>();
        listaDiariaMock.add(diario);

        RespaldoSemanal semanal = new RespaldoSemanal();
        semanal.setTotalRecaudado(50000.0);
        semanal.setCantidadVentas(10);
        listaSemanalMock = new ArrayList<>();
        listaSemanalMock.add(semanal);
    }

    @Test
    void cuandoRecolectarCierreDiarioEsExitoso_entoncesGuardaRespaldo() {
        when(ventasClient.obtenerCierreDelDia()).thenReturn(listaUbicacionesMock);

        orquestadorService.recolectarCierreDiario();

        // Ahora sí, verificará que el save() se ejecute exitosamente
        verify(diarioRepo, times(1)).save(any(RespaldoDiario.class));
    }

    @Test
    void cuandoRecolectarCierreDiarioFalla_entoncesLlamaEmergencia() {
        when(ventasClient.obtenerCierreDelDia()).thenThrow(new RuntimeException("Error simulado"));
        when(diarioRepo.findAll()).thenReturn(listaDiariaMock); // Para que la emergencia haga algo

        orquestadorService.recolectarCierreDiario();

        // Limpiamos la basura generada
        borrarPdfsGeneradosPorTest();
        
        // Comprobamos que el protocolo de emergencia operó (leyendo de BD)
        verify(diarioRepo, times(1)).findAll();
    }

    @Test
    void cuandoGenerarCierreSemanalConDatos_entoncesGeneraYBorra() {
        when(diarioRepo.findAll()).thenReturn(listaDiariaMock);

        orquestadorService.generarCierreSemanal();

        borrarPdfsGeneradosPorTest();

        verify(semanalRepo, times(1)).save(any(RespaldoSemanal.class));
        verify(diarioRepo, times(1)).deleteAll();
    }

    @Test
    void cuandoGenerarCierreSemanalSinDatos_entoncesNoHaceNada() {
        when(diarioRepo.findAll()).thenReturn(new ArrayList<>());
        
        orquestadorService.generarCierreSemanal();
        
        verify(semanalRepo, never()).save(any());
    }

    @Test
    void cuandoGenerarCierreMensualConDatos_entoncesGeneraYBorra() {
        when(semanalRepo.findAll()).thenReturn(listaSemanalMock);

        orquestadorService.generarCierreMensual();

        borrarPdfsGeneradosPorTest();

        verify(semanalRepo, times(1)).deleteAll();
    }

    @Test
    void cuandoGenerarCierreMensualSinDatos_entoncesNoHaceNada() {
        when(semanalRepo.findAll()).thenReturn(new ArrayList<>());
        
        orquestadorService.generarCierreMensual();
        
        verify(semanalRepo, never()).deleteAll();
    }

    // Método utilitario para limpiar la carpeta raíz de PDFs basura
    private void borrarPdfsGeneradosPorTest() {
        File folder = new File(".");
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles != null) {
            for (File file : listOfFiles) {
                if (file.isFile() && file.getName().endsWith(".pdf")) {
                    file.delete();
                }
            }
        }
    }
}