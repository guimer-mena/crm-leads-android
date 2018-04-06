package com.tecnologiaparainmobiliarias.centrodenotificacionestpi.preferencias;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.google.firebase.iid.FirebaseInstanceId;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.model.UsuarioLogin;

import java.io.IOException;

/**
 * Created by guime on 22/03/2018.
 */

public class PreferenciasUsuario {

    public static final String PREF_NAME = "USER_CRM";
    public static final String PREF_USER_ID = "PREF_ID";
    public static final String PREF_USER_CUENTA = "PREF_CUENTA";
    public static final String PREF_USER_CLAVE_SESION = "PREF_CLAVE_SESION";
    public static final String PREF_USER_FOTO = "PREF_FOTO";
    public static final String PREF_USER_NOMBRE = "PREF_NOMBRE";
    public static final String PREF_USAR_CORREO = "PREF_CORREO";
    public static final String PREF_USER_LOGO_EMPRESA = "PREF_LOGO_EMPRESA";
    public static final String PREF_USER_NOMBRE_EMPRESA = "PREF_NOMBRE_EMPRESA";

    private final SharedPreferences mPrefs;

    public boolean mIsLoggedIn = false;

    public static PreferenciasUsuario INSTANCE;

    public static PreferenciasUsuario get (Context context){
        if (INSTANCE == null){
            INSTANCE = new PreferenciasUsuario(context);
        }
        return INSTANCE;
    }

    private PreferenciasUsuario(Context context){
        mPrefs = context.getApplicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);

        mIsLoggedIn = !TextUtils.isEmpty(mPrefs.getString(PREF_USER_CLAVE_SESION, null));
    }

    public boolean isLoggedIn(){
        return mIsLoggedIn;
    }

    public void guardarUsuario(UsuarioLogin usuario){
        if (usuario != null){
            SharedPreferences.Editor editor = mPrefs.edit();
            editor.putString(PREF_USER_CLAVE_SESION, usuario.getClave_session());
            editor.putString(PREF_USER_ID, usuario.getId());
            editor.putString(PREF_USER_CUENTA, usuario.getCuenta());
            editor.putString(PREF_USER_NOMBRE, usuario.getNombre());
            editor.putString(PREF_USER_FOTO, usuario.getFoto());
            editor.putString(PREF_USAR_CORREO, usuario.getCorreo());
            editor.putString(PREF_USER_LOGO_EMPRESA, usuario.getLogo_empresa());
            editor.putString(PREF_USER_NOMBRE_EMPRESA, usuario.getNombre_empresa());

            editor.apply();

            mIsLoggedIn = true;

        }
    }

    public void LogOut(){
        mIsLoggedIn = false;
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.putString(PREF_USER_CLAVE_SESION, null);
        editor.putString(PREF_USER_ID, null);
        editor.putString(PREF_USER_CUENTA, null);
        editor.putString(PREF_USER_NOMBRE, null);
        editor.putString(PREF_USER_FOTO, null);
        editor.putString(PREF_USAR_CORREO, null);
        editor.putString(PREF_USER_NOMBRE_EMPRESA, null);
        editor.putString(PREF_USER_LOGO_EMPRESA, null);


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    FirebaseInstanceId.getInstance().deleteInstanceId();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        editor.apply();

    }

}
