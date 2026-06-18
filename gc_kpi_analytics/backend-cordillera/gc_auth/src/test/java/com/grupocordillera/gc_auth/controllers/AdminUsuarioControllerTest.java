package com.grupocordillera.gc_auth.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grupocordillera.gc_auth.config.JwtUtil;
import com.grupocordillera.gc_auth.dtos.UsuarioRequestDTO;
import com.grupocordillera.gc_auth.models.RolUsuario;
import com.grupocordillera.gc_auth.models.Usuario;
import com.grupocordillera.gc_auth.services.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AdminUsuarioControllerTest {

    private MockMvc mockMvc;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AdminUsuarioController adminUsuarioController;

    private ObjectMapper objectMapper;
    private UsuarioRequestDTO dtoMock;
    private Usuario usuarioMock;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        // Standalone Setup: Fuerza la prueba sin base de datos ni Spring Security global
        mockMvc = MockMvcBuilders.standaloneSetup(adminUsuarioController).build();

        dtoMock = new UsuarioRequestDTO();
        dtoMock.setEmail("nuevo@cordillera.cl");
        dtoMock.setPassword("12345");
        dtoMock.setNombre("Camilo");
        dtoMock.setRol(RolUsuario.ADMIN);

        usuarioMock = new Usuario();
        usuarioMock.setId(1L);
        usuarioMock.setEmail("nuevo@cordillera.cl");
        usuarioMock.setNombre("Camilo");
    }

    // --- TESTS CREAR USUARIO ---

    @Test
    void cuandoAdminCreaUsuario_entoncesRetorna201() throws Exception {
        when(jwtUtil.extraerRol("tokenAdmin")).thenReturn("ADMIN");
        when(authService.crearUsuario(any(Usuario.class))).thenReturn(usuarioMock);

        mockMvc.perform(post("/api/admin/usuarios/crear")
                .header("Authorization", "Bearer tokenAdmin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoMock)))
                .andExpect(status().isCreated());
    }

    @Test
    void cuandoNoAdminCreaUsuario_entoncesRetorna403() throws Exception {
        when(jwtUtil.extraerRol("tokenCajero")).thenReturn("CAJERO");

        mockMvc.perform(post("/api/admin/usuarios/crear")
                .header("Authorization", "Bearer tokenCajero")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoMock)))
                .andExpect(status().isForbidden())
                .andExpect(content().string("ACCESO DENEGADO: Solo los dueños/admins pueden crear usuarios."));
    }

    @Test
    void cuandoCrearFallaEnServicio_entoncesRetorna400() throws Exception {
        when(jwtUtil.extraerRol("tokenAdmin")).thenReturn("ADMIN");
        when(authService.crearUsuario(any(Usuario.class))).thenThrow(new RuntimeException("El correo ya está registrado"));

        mockMvc.perform(post("/api/admin/usuarios/crear")
                .header("Authorization", "Bearer tokenAdmin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoMock)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("El correo ya está registrado"));
    }

    // --- TESTS MODIFICAR USUARIO ---

    @Test
    void cuandoAdminModificaUsuario_entoncesRetorna200() throws Exception {
        when(jwtUtil.extraerRol("tokenAdmin")).thenReturn("ADMIN");
        when(authService.modificarUsuario(eq(1L), any(Usuario.class))).thenReturn(usuarioMock);

        mockMvc.perform(put("/api/admin/usuarios/modificar/1")
                .header("Authorization", "Bearer tokenAdmin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoMock)))
                .andExpect(status().isOk());
    }

    @Test
    void cuandoNoAdminModificaUsuarioO_TokenInvalido_entoncesRetorna403() throws Exception {
        // Probamos enviando un token que no empieza con "Bearer "
        mockMvc.perform(put("/api/admin/usuarios/modificar/1")
                .header("Authorization", "TokenMaloSinBearer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoMock)))
                .andExpect(status().isForbidden())
                .andExpect(content().string("ACCESO DENEGADO."));
    }

    @Test
    void cuandoModificarFallaEnServicio_entoncesRetorna400() throws Exception {
        when(jwtUtil.extraerRol("tokenAdmin")).thenReturn("ADMIN");
        when(authService.modificarUsuario(eq(1L), any(Usuario.class))).thenThrow(new RuntimeException("El usuario no existe"));

        mockMvc.perform(put("/api/admin/usuarios/modificar/1")
                .header("Authorization", "Bearer tokenAdmin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dtoMock)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("El usuario no existe"));
    }
}