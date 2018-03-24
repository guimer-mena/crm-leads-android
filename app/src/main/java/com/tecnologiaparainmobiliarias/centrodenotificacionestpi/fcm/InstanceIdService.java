package com.tecnologiaparainmobiliarias.centrodenotificacionestpi.fcm;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.CrmApi;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.model.ActualizarToken;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class InstanceIdService extends FirebaseInstanceIdService {

    private Retrofit mRestAdapter;
    private CrmApi mCrmApi;

    private static final String TAG = InstanceIdService.class.getSimpleName();

    public InstanceIdService() {
    }


    @Override
    public void onTokenRefresh() {
        String fcmToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, " TokenFCM: " + fcmToken);

        sendTokenToServer(fcmToken);
    }

    private void sendTokenToServer(String fcmToken) {
        //Acciones para enviar token al servidor
        //Crear conexion REST
        mRestAdapter = new Retrofit.Builder().baseUrl(CrmApi.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        //Crear conexion
        mCrmApi = mRestAdapter.create(CrmApi.class);
        //obtener valores guardados en preferencias

        SharedPreferences mPref = getSharedPreferences("USER_CRM", Context.MODE_PRIVATE);
        String claveSession = mPref.getString("PREF_CLAVE_SESION", "");
        String idusuario = mPref.getString("PREF_ID", "");
        String cuenta = mPref.getString("PREF_CUENTA", "");
        String iddispositivo = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        Call<ActualizarToken> registrarToken = mCrmApi.actualizarToken(cuenta, idusuario, fcmToken, claveSession, "ANDROID", iddispositivo);

        registrarToken.enqueue(new Callback<ActualizarToken>() {
            @Override
            public void onResponse(Call<ActualizarToken> call, Response<ActualizarToken> response) {
                if (!response.isSuccessful()) {
                    String error = "Ha ocurrido un error. Contacte al administrador.";
                    Log.d("ERROR_RETROFIT", error);
                } else {
                    if (response.body().getEstado().equals("SESSION_AUTORIZADA")) {

                        if (response.body().getMensaje() == "error agregar token" || response.body().getMensaje() == "error actualizar token") {
                            Log.d("ErrorActualizarToken", "Error al actualizar token");
                        }

                    } else {
                        Log.d("ErrorActualizarToken", "Session no autorizada");
                    }
                }
            }

            @Override
            public void onFailure(Call<ActualizarToken> call, Throwable t) {
                Log.d("Error_retrofit_token", t.getMessage() + " " + t.getLocalizedMessage());
            }

        });

    }
}
