package com.eliascapasso.auxilio.Modelo;

import com.eliascapasso.auxilio.Enumerados.EstadoPago;
import com.eliascapasso.auxilio.Enumerados.TipoPago;

public class Pago {
    private int id_pago;
    private TipoPago tipo;
    private EstadoPago estado;

    public Pago() {
        this.estado = EstadoPago.NO_PAGADO;
    }

    public int getId_pago() {
        return id_pago;
    }

    public void setId_pago(int id_pago) {
        this.id_pago = id_pago;
    }

    public EstadoPago getEstado() {
        return estado;
    }

    public void setEstado(EstadoPago estado) {
        this.estado = estado;
    }
}
