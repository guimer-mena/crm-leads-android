package com.tecnologiaparainmobiliarias.centrodenotificacionestpi.Splash;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.CrmApi;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.LoginTPIActivity;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.MainActivity;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.R;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.model.VerificarSesion;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.preferencias.PreferenciasUsuario;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SplashActivity extends AppCompatActivity {

    private Retrofit mRestAdaper;
    private CrmApi mCrmApi;

    private final int DURACION_SPLASH = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash);

        //Redireccion si no esta logueado
        if(!PreferenciasUsuario.get(this).isLoggedIn()){
            startActivity(new Intent(this,LoginTPIActivity.class));
            finish();
            return;
        }

        //Validar cuenta activa
        //Crear conexion REST
        mRestAdaper = new Retrofit.Builder().baseUrl(CrmApi.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        //Crear conexion
        mCrmApi = mRestAdaper.create(CrmApi.class);

        SharedPreferences mPref = getSharedPreferences("USER_CRM", Context.MODE_PRIVATE);
        String claveSession = mPref.getString("PREF_CLAVE_SESION", "");
        String cuenta = mPref.getString("PREF_CUENTA", "");

        Call<VerificarSesion> verificarSesion = mCrmApi.verificarSesion(cuenta, claveSession);

        verificarSesion.enqueue(new Callback<VerificarSesion>() {
            @Override
            public void onResponse(Call<VerificarSesion> call, Response<VerificarSesion> response) {
                Log.d("estadoSesion",response.body().getEstado());
                if (!response.isSuccessful()) {
                    String error = "Ha ocurrido un error. Contacte al administrador.";
                    Log.d("ERROR_RETROFIT", error);
                } else {
                    if (!response.body().getEstado().equals("SESSION_AUTORIZADA")) {
                        PreferenciasUsuario.get(SplashActivity.this).LogOut();
                        startActivity(new Intent(SplashActivity.this,LoginTPIActivity.class));
                        finish();
                        return;
                    }else{
                        new Handler().postDelayed(new Runnable(){
                            public void run(){
                                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            };
                        }, DURACION_SPLASH);
                    }
                }
            }

            @Override
            public void onFailure(Call<VerificarSesion> call, Throwable t) {
                Log.d("Error_retrofit_vsesion", t.getMessage() + " " + t.getLocalizedMessage());
                PreferenciasUsuario.get(SplashActivity.this).LogOut();
                startActivity(new Intent(SplashActivity.this,LoginTPIActivity.class));
                finish();
                return;
            }
        });


    }
}
