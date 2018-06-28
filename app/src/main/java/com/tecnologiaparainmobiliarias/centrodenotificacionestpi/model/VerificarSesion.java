package com.tecnologiaparainmobiliarias.centrodenotificacionestpi.model;

import java.util.Date;

/**
 * Created by guime on 23/03/2018.
 */

public class VerificarSesion {
    private String estado;
    private String medio;
    private String fecha_ingreso;

    public VerificarSesion(String estado, String medio, String fecha_ingreso) {
        this.estado = estado;
        this.medio = medio;
        this.fecha_ingreso = fecha_ingreso;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getMedio() { return medio; }

    public void  setMedio(String medio) { this.medio = medio; }

    public String getFecha_ingreso() {
        return fecha_ingreso;
    }

    public void setFecha_ingreso(String fecha_ingreso) {
        this.fecha_ingreso = fecha_ingreso;
    }
}

