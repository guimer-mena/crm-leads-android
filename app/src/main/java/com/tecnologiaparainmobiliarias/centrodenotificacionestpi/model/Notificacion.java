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
    private Date Fecha;
    private String Categoria;
    private String Subcategoria;
    private String Visto;
    private String UrlReagendar;
    private String UrlFinalizar;

    public Notificacion(){}

    public Notificacion(String titulo,String descripcion, String url, String logo, Date fecha, String categoria, String subcategoria,String urlreagendar, String urlfinalizar, String visto) {
    //public Notificacion(String titulo) {
        Id = RealmHelper.NotificacionId.incrementAndGet();
        Titulo = titulo;
        DescripcionComleta = descripcion;
        Descripcion = descripcion;
        Url = url;
        Icono = logo;
        Fecha = fecha;
        Visto = visto;
        Categoria = categoria;
        Subcategoria = subcategoria;
        UrlFinalizar = urlfinalizar;
        UrlReagendar = urlreagendar;

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

    public Date getFecha() {
        return Fecha;
    }

    public void setFecha(Date  fecha) {
        Fecha = fecha;
    }

    public String getVisto(){ return Visto; }

    public void setVisto(String visto){ Visto = visto; }

    public String getCategoria(){
        return Categoria;
    }

    public void setCategoria(String categoria){
        Categoria = categoria;
    }

    public String getSubcategoria(){
        return Subcategoria;
    }

    public void setSubcategoria(String subcategoria){
        Subcategoria = subcategoria;
    }

    public String getUrlReagendar(){
        return  UrlReagendar;
    }
    public void setUrlReagendar(String urlreagendar){
        UrlReagendar = urlreagendar;
    }

    public String getUrlFinalizar(){
        return UrlFinalizar;
    }
    public void setUrlFinalizar(String urlFinalizar){
        UrlFinalizar = urlFinalizar;
    }

}
