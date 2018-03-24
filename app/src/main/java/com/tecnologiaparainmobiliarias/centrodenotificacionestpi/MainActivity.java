package com.tecnologiaparainmobiliarias.centrodenotificacionestpi;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;

import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.model.VerificarSesion;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.notificaciones.NotificationsFragment;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.notificaciones.NotificationsPresenter;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.preferencias.PreferenciasUsuario;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Retrofit mRestAdaper;
    private CrmApi mCrmApi;

    private static final String TAG = MainActivity.class.getSimpleName();

    private NotificationsFragment mNotificationFragment;
    private NotificationsPresenter mNotificationPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Redireccion si no eesta logueado
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
                        PreferenciasUsuario.get(MainActivity.this).LogOut();
                        startActivity(new Intent(MainActivity.this,LoginTPIActivity.class));
                        finish();
                        return;
                    }
                }
            }

            @Override
            public void onFailure(Call<VerificarSesion> call, Throwable t) {
                Log.d("Error_retrofit_vsesion", t.getMessage() + " " + t.getLocalizedMessage());
            }
        });


        //Mostrar notificaciones
        mNotificationFragment = (NotificationsFragment) getSupportFragmentManager().findFragmentById(R.id.notifications_container);

        if(mNotificationFragment == null){
            mNotificationFragment = NotificationsFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.notifications_container,mNotificationFragment).commit();
        }

        mNotificationPresenter = new NotificationsPresenter(mNotificationFragment, FirebaseMessaging.getInstance());

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.nav_cerrar_sesion){
            PreferenciasUsuario.get(MainActivity.this).LogOut();
            if(!PreferenciasUsuario.get(this).isLoggedIn()){
                startActivity(new Intent(this, LoginTPIActivity.class));
            }
        }

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }
}
