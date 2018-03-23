package com.tecnologiaparainmobiliarias.centrodenotificacionestpi.data;

import java.util.UUID;

/**
 * Created by guime on 22/03/2018.
 */

public class PushNotification {
    private String id;
    private String titulo;
    private String descripcion;
    private String fecha;
    private String url;

    public PushNotification(){
        id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
