package com.tecnologiaparainmobiliarias.centrodenotificacionestpi;

import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.iid.FirebaseInstanceIdService;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.Helper.ImageHelper;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.model.VerificarSesion;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.notificaciones.NotificationsFragment;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.notificaciones.NotificationsPresenter;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.preferencias.PreferenciasUsuario;

import java.io.FilenameFilter;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private NotificationsFragment mNotificationFragment;
    private NotificationsPresenter mNotificationPresenter;
    private NavigationView navigationView;



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
        navigationView.getMenu().getItem(0).setChecked(true);

        //Mostrar notificaciones
        mNotificationFragment = (NotificationsFragment) getSupportFragmentManager().findFragmentById(R.id.notifications_container);

        if(mNotificationFragment == null){


            mNotificationFragment = NotificationsFragment.newInstance();
            getSupportFragmentManager().beginTransaction().add(R.id.notifications_container,mNotificationFragment).commit();
        }

        mNotificationPresenter = new NotificationsPresenter(mNotificationFragment, FirebaseMessaging.getInstance(),getApplicationContext());


        Bitmap avatarBitmap = BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_user_no_photo);


        Bitmap croppedImage = ImageHelper.cropBitmapToSquare(avatarBitmap);

        Bitmap redondearContornoAvatar = ImageHelper.getRoundedCornerBitmap(croppedImage, 1000);


        SharedPreferences mPref = getSharedPreferences("USER_CRM", Context.MODE_PRIVATE);
        String nombreUsuario = mPref.getString("PREF_NOMBRE", null);
        String correo = mPref.getString("PREF_CORREO", null);
        String fotoUsuario = mPref.getString("PREF_FOTO", null);
        String logoEmpresa = mPref.getString("PREF_LOGO_EMPRESA", null);
        String nombreEmpresa = mPref.getString("PREF_NOMBRE_EMPRESA", null);



        TextView txtNombreUsuario;
        TextView txtCorreoUsuario;
        ImageView imgAvatar;

        View navHeader = navigationView.getHeaderView(0);
        txtNombreUsuario = navHeader.findViewById(R.id.txtNombreUsuario);
        txtCorreoUsuario = navHeader.findViewById(R.id.txtCorreoUsuario);
        imgAvatar = navHeader.findViewById(R.id.imageViewAvatar);


        ImageView imgLogoEmpresa = (ImageView) findViewById(R.id.imgLogoEmpresa);
        //TextView txtNombreEmpresa = (TextView) findViewById(R.id.txtNombreEmpresa);

        String filenameArray[] = fotoUsuario.split("/");
        String extension = filenameArray[filenameArray.length-2];
        System.out.println(extension);

        if(fotoUsuario != null && fotoUsuario != "http://fotos.crminmobiliario.com/"){
            //Picasso.with(this).load(fotoUsuario).resize(150,150).transform(new CropCircleTransformation()).centerCrop().into(imgAvatar);
            Picasso.with(this).load(fotoUsuario).resize(400,400).transform(new CropCircleTransformation()).centerCrop().into(imgAvatar);
        }

        if(logoEmpresa != null) {

            Picasso.with(this).load(logoEmpresa).resize(400, 400).transform(new CropCircleTransformation()).centerCrop().into(imgLogoEmpresa);
        }

        toolbar.setTitle(nombreEmpresa);


        txtNombreUsuario.setText(nombreUsuario.toString());
        txtCorreoUsuario.setText(correo.toString());


        CollapsingToolbarLayout collapser = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        collapser.setTitle(nombreEmpresa);



        /*SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        StringBuilder builder = new StringBuilder();

        builder.append("\n" + "Perform Sync:\t" + sharedPrefs.getBoolean("perform_sync", false));
        builder.append("\n" + "Sync Intervals:\t" + sharedPrefs.getString("sync_interval", "-1"));
        builder.append("\n" + "Name:\t" + sharedPrefs.getString("full_name", "Not known to us"));
        builder.append("\n" + "Email Address:\t" + sharedPrefs.getString("email_address", "No EMail Address Provided"));
        builder.append("\n" + "Customized Notification Ringtone:\t" + sharedPrefs.getString("notification_ringtone", ""));
        builder.append("\n\nClick on Settings Button at bottom right corner to Modify Your Prefrences");
        builder.append("\n\nLeads JAUS:\t"+sharedPrefs.getBoolean("pref_notificacion_leads_de_sitio_web_jaus", false));

        Log.d("Configuracion :", builder.toString());*/


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

        if(id == R.id.nav_configuracion){
            Intent modifySettings=new Intent(MainActivity.this,SettingsActivity.class);
            startActivity(modifySettings);
        }


        //Filtros de notificaciones
        if(id == R.id.nav_todos){
            mNotificationPresenter.Filtro = "todos";
            mNotificationPresenter.loadNotifications("todos");
        }

        if(id == R.id.nav_bolsa24siete){
            mNotificationPresenter.Filtro = "bolsa24siete";
            mNotificationPresenter.loadNotifications("bolsa24siete");
        }

        if(id == R.id.nav_leads){
            mNotificationPresenter.Filtro = "leads";
            mNotificationPresenter.loadNotifications("leads");
        }

        if(id == R.id.nav_sistema){
            mNotificationPresenter.Filtro = "sistema";
            mNotificationPresenter.loadNotifications("sistema");
        }

        if (id == R.id.nav_otros) {
            mNotificationPresenter.Filtro = "otros";
            mNotificationPresenter.loadNotifications("otros");
        }

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }


}
