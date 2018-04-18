package com.tecnologiaparainmobiliarias.centrodenotificacionestpi.model.Realm;

import android.content.Context;
import android.util.Log;

import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.model.Notificacion;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class RealmHelper {
    private Realm realm;
    private Context context;
    private RealmResults<Notificacion> realmResults;

    public RealmHelper(Context context){
        Realm.init(context);
        this.context = context;
        RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
        realm = Realm.getInstance(config);

    }

    private int getCount(){
        realmResults = realm.where(Notificacion.class).findAll();
        realmResults.sort("Id");
        return realmResults.size();
    }

    public ArrayList<Notificacion> showAllNotifications(){
        int id;
        String titulo;
        String descripcion;
        String fecha;
        String url;
        String icono;

        ArrayList<Notificacion> data = new ArrayList<>();

        realmResults = realm.where(Notificacion.class).findAll();
        realmResults.sort("Id");

        if(realmResults.size() > 0){
            for (int i = 0; i < realmResults.size(); i++){

                titulo = realmResults.get(i).getTitulo();
                descripcion = realmResults.get(i).getDescripcion();
                fecha = realmResults.get(i).getFecha();
                url = realmResults.get(i).getUrl();
                icono = realmResults.get(i).getIcono();

                data.add( new Notificacion(titulo, descripcion,url,icono,fecha));

            }
        }

        return data;
    }

    public void addNewNotification(String name, String descipcion, String fecha, String url, String logo) {
        Notificacion data = new Notificacion();
        data.setId(getCount() + 1);
        data.setTitulo(name);
        data.setDescripcion(descipcion);
        data.setUrl(url);
        data.setDescripcionComleta(descipcion);
        data.setFecha(fecha);
        data.setIcono(logo);


        realm.beginTransaction();
        realm.copyToRealm(data);
        realm.commitTransaction();
        //Log.d("Guardar_Notificacion", "Notificacion Guardada: "+name+" / "+descipcion+" / "+fecha+" / "+ url + " / "+logo);
    }
    public void deleteNotification(int id){
        RealmResults<Notificacion> dataRealm = realm.where(Notificacion.class).equalTo("Id", id).findAll();
        realm.beginTransaction();
        dataRealm.remove(0);
        dataRealm.clear();
        realm.commitTransaction();
    }
}
