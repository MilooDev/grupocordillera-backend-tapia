package com.grupocordillera.gc_auth.services;

import com.grupocordillera.gc_auth.config.JwtUtil;
import com.grupocordillera.gc_auth.models.Usuario;
import com.grupocordillera.gc_auth.models.RolUsuario; // Asegúrate de que esta importación sea correcta
import com.grupocordillera.gc_auth.repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    private Usuario usuarioPrueba;

    @BeforeEach
    void setUp() {
        usuarioPrueba = new Usuario();
        usuarioPrueba.setId(1L);
        usuarioPrueba.setNombre("Camilo");
        usuarioPrueba.setEmail("admin@cordillera.cl");
        usuarioPrueba.setPassword("clave_encriptada_123");
        // ¡ESTA LÍNEA ES VITAL PARA QUE NO DE NULL POINTER!
        usuarioPrueba.setRol(com.grupocordillera.gc_auth.models.RolUsuario.ADMIN);
    }

    // --- TESTS DE LOGIN ---

    @Test
    void cuandoLoginEsExitoso_entoncesRetornaToken() {
        when(usuarioRepository.findByEmail("admin@cordillera.cl")).thenReturn(Optional.of(usuarioPrueba));
        when(passwordEncoder.matches("12345", "clave_encriptada_123")).thenReturn(true);
        // Evitamos el NullPointerException si el rol no está instanciado en el test
        when(jwtUtil.generarToken(eq("admin@cordillera.cl"), any())).thenReturn("token.jwt.mock");

        String token = authService.login("admin@cordillera.cl", "12345");

        assertEquals("token.jwt.mock", token);
    }

    @Test
    void cuandoEmailNoExiste_entoncesLanzaExcepcion() {
        when(usuarioRepository.findByEmail("fantasma@cordillera.cl")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.login("fantasma@cordillera.cl", "12345");
        });

        assertEquals("Usuario no encontrado", exception.getMessage());
        verify(passwordEncoder, never()).matches(anyString(), anyString());
    }

    @Test
    void cuandoPasswordEsIncorrecto_entoncesLanzaExcepcion() {
        when(usuarioRepository.findByEmail("admin@cordillera.cl")).thenReturn(Optional.of(usuarioPrueba));
        when(passwordEncoder.matches("claveMala", "clave_encriptada_123")).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.login("admin@cordillera.cl", "claveMala");
        });

        assertEquals("Contraseña incorrecta", exception.getMessage());
    }

    // --- TESTS DE CREAR USUARIO ---

    @Test
    void cuandoCreaUsuarioNuevo_entoncesLoGuardaEncriptado() {
        when(usuarioRepository.findByEmail(usuarioPrueba.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(usuarioPrueba.getPassword())).thenReturn("nuevo_hash");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioPrueba);

        Usuario guardado = authService.crearUsuario(usuarioPrueba);

        assertNotNull(guardado);
        verify(usuarioRepository, times(1)).save(usuarioPrueba);
    }

    @Test
    void cuandoCreaUsuarioDuplicado_entoncesLanzaExcepcion() {
        when(usuarioRepository.findByEmail(usuarioPrueba.getEmail())).thenReturn(Optional.of(usuarioPrueba));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.crearUsuario(usuarioPrueba);
        });

        assertEquals("El correo ya está registrado", exception.getMessage());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    // --- TESTS DE MODIFICAR USUARIO ---

    @Test
    void cuandoModificaUsuarioConPassword_entoncesActualizaYEncripta() {
        Usuario datosNuevos = new Usuario();
        datosNuevos.setNombre("Camilo Modificado");
        datosNuevos.setPassword("nuevaClave");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioPrueba));
        when(passwordEncoder.encode("nuevaClave")).thenReturn("hash_nuevo");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioPrueba);

        authService.modificarUsuario(1L, datosNuevos);

        verify(passwordEncoder, times(1)).encode("nuevaClave");
        verify(usuarioRepository, times(1)).save(usuarioPrueba);
    }

    @Test
    void cuandoModificaUsuarioSinPassword_entoncesActualizaSinEncriptar() {
        Usuario datosNuevos = new Usuario();
        datosNuevos.setNombre("Camilo Modificado");
        datosNuevos.setPassword(""); // Viene vacío

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioPrueba));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioPrueba);

        authService.modificarUsuario(1L, datosNuevos);

        verify(passwordEncoder, never()).encode(anyString()); // Verifica que no intentó encriptar nada
        verify(usuarioRepository, times(1)).save(usuarioPrueba);
    }

    @Test
    void cuandoModificaUsuarioQueNoExiste_entoncesLanzaExcepcion() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            authService.modificarUsuario(99L, new Usuario());
        });

        assertEquals("El usuario no existe", exception.getMessage());
    }
}