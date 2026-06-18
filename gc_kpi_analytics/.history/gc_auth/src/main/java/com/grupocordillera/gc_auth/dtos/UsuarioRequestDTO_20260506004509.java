package com.grupocordillera.gc_auth.dtos;

import com.grupocordillera.gc_auth.models.RolUsuario;

public class UsuarioRequestDTO {
    private String email;
    private String password;
    private String nombre;
    private RolUsuario rol;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public RolUsuario getRol() {
        return rol;
    }

    public void setRol(RolUsuario rol) {
        this.rol = rol;
    }
}