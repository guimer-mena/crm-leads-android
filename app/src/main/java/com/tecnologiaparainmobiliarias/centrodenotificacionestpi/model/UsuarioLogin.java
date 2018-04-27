package com.tecnologiaparainmobiliarias.centrodenotificacionestpi.model;

/**
 * Created by guime on 22/03/2018.
 */

public class UsuarioLogin {
    private String id;
    private String cuenta;
    private String nombre;
    private String correo;
    private String estado;
    private String telefono;
    private String clave_session;
    private String foto;
    private String logo_empresa;
    private String nombre_empresa;


    public UsuarioLogin(String id, String cuenta, String nombre, String correo, String conexion, String telefono, String clave_session, String foto,String logo_empresa, String nombre_empresa){

        this.id = id;
        this.cuenta = cuenta;
        this.nombre = nombre;
        this.correo = correo;
        this.estado = conexion;
        this.telefono = telefono;
        this.clave_session = clave_session;
        this.foto = foto;
        this.nombre_empresa = nombre_empresa;
        this.logo_empresa = logo_empresa;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCuenta() {
        return cuenta;
    }

    public void setCuenta(String cuenta) {
        this.cuenta = cuenta;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getEstado(){
        return estado;
    }
    public void setEstado(String estado){
        this.estado = estado;
    }

    public String getClave_session() {
        return clave_session;
    }

    public void setClave_session(String clave_sesion) {
        this.clave_session = clave_sesion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getLogo_empresa() {
        return logo_empresa;
    }

    public void setLogo_empresa(String logoEmpresa) {
        this.logo_empresa = logoEmpresa;
    }

    public String getNombre_empresa() {
        return nombre_empresa;
    }

    public void setNombre_empresa(String nombreEmpresa) {
        this.nombre_empresa = nombreEmpresa;
    }
}
