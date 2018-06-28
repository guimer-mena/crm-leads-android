package com.tecnologiaparainmobiliarias.centrodenotificacionestpi.Splash;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.CrmApi;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.LoginTPIActivity;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.MainActivity;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.R;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.model.VerificarSesion;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.preferencias.PreferenciasUsuario;

import java.security.KeyStore;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SplashActivity extends AppCompatActivity {

    private Retrofit mRestAdaper;
    private CrmApi mCrmApi;

    private final int DURACION_SPLASH = 1000;

    private String idAsesor = null;
    private String claveSesion = null;
    private String numeroCueta = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        SharedPreferences mPref = getSharedPreferences("USER_CRM", Context.MODE_PRIVATE);
        claveSesion = mPref.getString("PREF_CLAVE_SESION", "");
        numeroCueta = mPref.getString("PREF_CUENTA", "");
        idAsesor = mPref.getString("PREF_ID","");

        setContentView(R.layout.activity_splash);

        //Redireccion si no esta logueado
        if(!PreferenciasUsuario.get(this).isLoggedIn()){
            Intent intentLogin = new Intent(this,LoginTPIActivity.class);
            intentLogin.putExtra("estado_sesion", "Primero");
            startActivity(intentLogin);
            finish();
            return;
        }

        //Validar cuenta activa
        //Crear conexion REST
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .create();
        mRestAdaper = new Retrofit.Builder().baseUrl(CrmApi.BASE_URL).addConverterFactory(GsonConverterFactory.create(gson)).build();
        //Crear conexion
        mCrmApi = mRestAdaper.create(CrmApi.class);



        Call<VerificarSesion> verificarSesion = mCrmApi.verificarSesion(numeroCueta, idAsesor, claveSesion);

        verificarSesion.enqueue(new Callback<VerificarSesion>() {
            @Override
            public void onResponse(Call<VerificarSesion> call, Response<VerificarSesion> response) {
                Log.d("estadoSesion",response.body().getEstado());
                if (!response.isSuccessful()) {
                    String error = "Ha ocurrido un error. Contacte al administrador.";
                    Log.d("ERROR_RETROFIT", error);
                } else {
                    if (!response.body().getEstado().equals("SESSION_AUTORIZADA")) {
                    //if(true){
                        PreferenciasUsuario.get(SplashActivity.this).LogOut2();
                        final Intent intentLogin = new Intent(SplashActivity.this,LoginTPIActivity.class);

                        //startActivity(new Intent(SplashActivity.this,LoginTPIActivity.class));
                        //startActivity(intentLogin);

                        //finish();
                        //return;
                        Log.d("EstadoBundle",response.body().getEstado().toString());
                        //Validar diferentes estados para redireccion
                        if(response.body().getEstado().equals("ERROR_CUENTA_USUARIO_SUSPENDIDA")){
                        //if(true){
                            intentLogin.putExtra("estado_sesion", response.body().getEstado().toString());

                            AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
                            builder.setTitle("Importante");
                            builder.setMessage("Su cuenta de susario se encuentra actualmente suspendida.\n\nContacte con su administrador para resolver el problema.");
                            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(intentLogin);
                                }
                            });
                            builder.create();
                            builder.show();
                        }else if(response.body().getEstado().equals("ERROR_NO_MOVIL")){
                            intentLogin.putExtra("estado_sesion",response.body().getEstado().toString());

                            AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
                            builder.setTitle("Importante");
                            builder.setMessage("Su cuenta es incompatible con esta versión de la aplicación.");
                            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(intentLogin);
                                }
                            });
                            builder.create();
                            builder.show();
                        }else if(response.body().getEstado().equals("SESSION_INICIADA_EN_OTRO_DISPOSITIVO")){
                            intentLogin.putExtra("estado_sesion", response.body().getEstado().toString());
                            intentLogin.putExtra("fecha_ultimo_ingreso", response.body().getFecha_ingreso());
                            intentLogin.putExtra("medio_ingreso",response.body().getMedio());
                            intentLogin.putExtra("tipo_accion", "VERIFICACION_SESION");
                            startActivity(intentLogin);
                        }else{
                            intentLogin.putExtra("estado_sesion", response.body().getEstado().toString());
                            startActivity(intentLogin);
                        }


                    }else{
                        new Handler().postDelayed(new Runnable(){
                            public void run(){
                                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                                //sintent.putExtra("estado_sesion","NoEntro");
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
                PreferenciasUsuario.get(SplashActivity.this).LogOut2();
                Intent intentLogin = new Intent(SplashActivity.this,LoginTPIActivity.class);
                intentLogin.putExtra("estado_sesion","NoEntro2");
                startActivity(intentLogin);
                finish();
                return;
            }
        });


    }

    private void showMessageLogin(String mensaje){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Importante");
        builder.setMessage(mensaje);
        builder.setPositiveButton("Aceptar",null);
        builder.create();
        builder.show();
    }

    private boolean isOnline(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }
}
