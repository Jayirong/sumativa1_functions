package com.sumativa1.model;

import java.time.LocalDateTime;

public class LoginFallidoEvent {
    private String emailUsuario;
    private int cantidadIntentos;
    private LocalDateTime fecha;

    public String getEmailUsuario() { return emailUsuario; }
    public void setEmailUsuario(String emailUsuario) { this.emailUsuario = emailUsuario; }

    public int getCantidadIntentos() { return cantidadIntentos; }
    public void setCantidadIntentos(int cantidadIntentos) { this.cantidadIntentos = cantidadIntentos; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
}
