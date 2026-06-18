package com.grupocordillera.gc_auth.models;

public enum RolUsuario {
    GERENTE,   // Acceso total y reportes analíticos de Python
    ADMIN,     // Gestión del sistema y usuarios
    CAJERO,    // Punto de Venta / Cajas (Reemplaza al antiguo Vendedor)
    BODEGUERO  // Logística e Inventario
}