package com.tecnologiaparainmobiliarias.centrodenotificacionestpi.app;

import android.app.Application;

import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.model.Notificacion;

import java.util.concurrent.atomic.AtomicInteger;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmResults;


public class MiAplicacion extends Application {

    public static AtomicInteger NotificacionId = new AtomicInteger();

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
        setUpRealConfig();

        Realm realm = Realm.getDefaultInstance();
        NotificacionId = getIdByTable(realm, Notificacion.class);
        realm.close();


    }

    private void setUpRealConfig(){
        //Realm.init(getApplicationContext());
        RealmConfiguration config = new RealmConfiguration.Builder().name("test1").schemaVersion(1).deleteRealmIfMigrationNeeded().build();
        //RealmConfiguration config = new RealmConfiguration.Builder(getApplicationContext()).deleteRealmIfMigrationNeeded().build();
        Realm.setDefaultConfiguration(config);
    }

    private <T extends RealmObject> AtomicInteger getIdByTable(Realm realm, Class<T> anyClass){

        RealmResults<T> results = realm.where(anyClass).findAll();
        return (results.size() > 0 )? new AtomicInteger(results.max("Id").intValue()) : new AtomicInteger();

    }
}
