package com.tecnologiaparainmobiliarias.centrodenotificacionestpi;

import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.model.ActualizarToken;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.model.Retrofit.ObtenerTokenActivo;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.model.UsuarioLogin;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.model.VerificarSesion;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by guime on 22/03/2018.
 */

public interface CrmApi {
    /*
    TODO: Cambiar host por "10.0.3.2" para Genymotion.
    TODO: Cambiar host por "10.0.2.2" para AVD.
    TODO: Cambiar host por IP de tu PC para dispositivo real.
    */

    public static final String BASE_URL = "http://10.0.2.2:8008/webservice/1.1/crm/rest/";
    //public static final String BASE_URL = "http://192.168.1.96:8011/webservice/1.1/crm/rest/";

    @FormUrlEncoded
    @POST("usuarios/validar_acceso_simple")
    Call<UsuarioLogin> login(@Field("numeroCuenta") String cuenta, @Field("usuario") String usuario, @Field("contrasenia") String contrasenia,@Field("tipo_acceso") String tipo_acceso, @Field("dispositivo_id") String dispositivo_id );
    //Call<UsuarioLogin> login(@Field("numeroCuenta") String cuenta, @Field("usuario") String usuario, @Field("contrasenia") String contrasenia );

    @FormUrlEncoded
    @POST("usuarios/actualizar_token_notificaciones")
    Call<ActualizarToken> actualizarToken(@Field("numeroCuenta") String cuenta, @Field("idusuario") String idusuario, @Field("token") String token, @Field("clave_session") String clave_session, @Field("dispositivo") String dispositivo, @Field("iddispositivo") String ididspositivo);

    @FormUrlEncoded
    @POST("usuarios/verificar_sesion_activa")
    Call<VerificarSesion> verificarSesion(@Field("numeroCuenta") String cuenta, @Field("claveSession") String claveSession);

    @FormUrlEncoded
    @POST("usuarios/obtener_token_activo")
    Call<ObtenerTokenActivo> obtenerTokenActivo(@Field("numeroCuenta") String cuenta, @Field("claveSession") String clave, @Field("idUsuario") String usuario, @Field("tipoAcceso") String tipoAcceso, @Field("dispositivoId") String dispositivoId);
}
