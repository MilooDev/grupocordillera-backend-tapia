package com.grupocordillera.gc_auth.models;

public enum RolUsuario {
    ROLE_ADMIN, // Dueños y Gerentes (Acceso total al Dashboard, reportes y creación de
                // usuarios)
    ROLE_VENDEDOR, // Punto de Venta / Cajas (Solo buscar productos y registrar ventas)
    ROLE_BODEGUERO // Logística (Solo gestionar el stock e ingresar nuevos productos)
}