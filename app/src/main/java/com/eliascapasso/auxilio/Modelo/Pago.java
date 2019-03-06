package com.eliascapasso.auxilio.Modelo;

import com.eliascapasso.auxilio.Enumerados.EstadoPago;

public class Pago {
    private int dni_usuario;
    private int id_curso;
    private EstadoPago estado;

    public Pago() {
        this.estado = EstadoPago.NO_PAGADO;
    }

    public int getDni_usuario() {
        return dni_usuario;
    }

    public void setDni_usuario(int dni_usuario) {
        this.dni_usuario = dni_usuario;
    }

    public int getId_curso() {
        return id_curso;
    }

    public void setId_curso(int id_curso) {
        this.id_curso = id_curso;
    }

    public EstadoPago getEstado() {
        return estado;
    }

    public void setEstado(EstadoPago estado) {
        this.estado = estado;
    }
}
