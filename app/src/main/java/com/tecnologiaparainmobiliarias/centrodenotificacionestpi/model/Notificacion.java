package com.tecnologiaparainmobiliarias.centrodenotificacionestpi.model;

import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.app.MiAplicacion;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.model.Realm.RealmHelper;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Notificacion  extends RealmObject{

    @PrimaryKey
    private int Id;
    private String Titulo;
    private String DescripcionComleta;
    private String Descripcion;
    private String Url;
    private String Icono;
    private String Fecha;

    public Notificacion(){}

    public Notificacion(String titulo,String descripcion, String url, String logo, String fecha) {
    //public Notificacion(String titulo) {
        Id = RealmHelper.NotificacionId.incrementAndGet();
        Titulo = titulo;
        DescripcionComleta = descripcion;
        Descripcion = descripcion;
        Url = url;
        Icono = logo;
        Fecha = fecha;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getTitulo() {
        return Titulo;
    }

    public void setTitulo(String titulo) {
        Titulo = titulo;
    }

    public String getDescripcionComleta() {
        return DescripcionComleta;
    }

    public void setDescripcionComleta(String descripcionComleta) {
        DescripcionComleta = descripcionComleta;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String descripcion) {
        Descripcion = descripcion;
    }

    public String getUrl() {
        return Url;
    }

    public void setUrl(String url) {
        Url = url;
    }

    public String getIcono() {
        return Icono;
    }

    public void setIcono(String icono) {
        this.Icono = icono;
    }

    public String getFecha() {
        return Fecha;
    }

    public void setFecha(String  fecha) {
        Fecha = fecha;
    }
}
