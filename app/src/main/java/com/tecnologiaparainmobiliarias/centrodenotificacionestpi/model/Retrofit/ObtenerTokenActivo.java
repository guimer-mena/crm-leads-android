package com.tecnologiaparainmobiliarias.centrodenotificacionestpi.model.Retrofit;

public class ObtenerTokenActivo {
    private String clave_session;

    public ObtenerTokenActivo(String clave_session) {
        this.clave_session = clave_session;
    }

    public String getClave_session() {
        return clave_session;
    }

    public void setClave_session(String clave_session) {
        this.clave_session = clave_session;
    }
}
