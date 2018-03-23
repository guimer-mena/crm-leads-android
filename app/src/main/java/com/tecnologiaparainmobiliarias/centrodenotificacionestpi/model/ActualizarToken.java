package com.tecnologiaparainmobiliarias.centrodenotificacionestpi.model;

/**
 * Created by guime on 22/03/2018.
 */

public class ActualizarToken {
    private String estado;
    private String mensaje;

    public ActualizarToken(String estado, String mensaje) {
        this.estado = estado;
        this.mensaje = mensaje;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
}
