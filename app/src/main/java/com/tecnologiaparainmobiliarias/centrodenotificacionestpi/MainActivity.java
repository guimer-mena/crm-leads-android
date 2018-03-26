package com.tecnologiaparainmobiliarias.centrodenotificacionestpi;

import android.content.Context;
import android.content.Intent;

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


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

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
