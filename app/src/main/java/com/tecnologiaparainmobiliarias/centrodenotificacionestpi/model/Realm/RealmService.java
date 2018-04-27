package com.tecnologiaparainmobiliarias.centrodenotificacionestpi.model.Realm;

import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.model.Notificacion;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;

public class RealmService {
    private final Realm mRealm;

    public RealmService(final Realm realm){
        mRealm = realm;
    }

    public void closeRealm(){
        mRealm.close();
    }

    public RealmResults<Notificacion> getAllNotifications(){
        return mRealm.where(Notificacion.class).findAll();
    }
    public Notificacion getNotificacion(final int IdNotificacion){
        return mRealm.where(Notificacion.class).equalTo("Id",IdNotificacion).findFirst();
    }

    public void addNotificationAsync(final String titulo, final String descipcion, final Date fecha, final String url, final String logo, final OnTransactionCallback onTransactionCallback){

        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Notificacion notificacion = realm.createObject(Notificacion.class);

                notificacion.setTitulo(titulo);
                notificacion.setDescripcion(descipcion);
                notificacion.setDescripcionComleta(descipcion);
                notificacion.setFecha(fecha);
                notificacion.setIcono(logo);
                notificacion.setUrl(url);
            }
        });
    }



    public interface OnTransactionCallback{
        void onRelmSuccess();
        void onRealmError(final Exception e);
    }
}
