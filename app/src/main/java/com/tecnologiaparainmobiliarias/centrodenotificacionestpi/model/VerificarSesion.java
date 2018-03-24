package com.tecnologiaparainmobiliarias.centrodenotificacionestpi.model;

/**
 * Created by guime on 23/03/2018.
 */

public class VerificarSesion {
    private String estado;

    public VerificarSesion(String estado) {
        this.estado = estado;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
