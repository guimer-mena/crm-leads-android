package com.tecnologiaparainmobiliarias.centrodenotificacionestpi.notificaciones;

import android.content.Context;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.MainActivity;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.data.PushNotification;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.data.PushNotificationsRepository;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.model.Notificacion;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.model.Realm.RealmHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by guime on 22/03/2018.
 */

public class NotificationsPresenter implements NotificationsContract.Presenter {

    private final NotificationsContract.View mNotificationView;
    private final FirebaseMessaging mFCMInteractor;
    private Realm realm;

    public  String Filtro = "todos";

    private RealmHelper realmHelper;

    public NotificationsPresenter(NotificationsContract.View mNotificationView, FirebaseMessaging mFCMInteractor, Context context) {
        this.mNotificationView = mNotificationView;
        this.mFCMInteractor = mFCMInteractor;
        realmHelper = new RealmHelper(context);

        mNotificationView.setPresenter(this);
    }

    @Override
    public void start() {
        registerAppClient();
        loadNotifications(Filtro);
    }

    @Override
    public void registerAppClient() {
        mFCMInteractor.subscribeToTopic("notificaciones");
    }

    @Override
    public void loadNotifications(String filtro) {

        /*PushNotificationsRepository.getInstance().getPushNotifications(new PushNotificationsRepository.LoadCallback() {
            @Override
            public void onLoaded(ArrayList<Notificacion> notifications) {
                if (notifications.size() > 0){
                    mNotificationView.showEmptyState(false);
                    mNotificationView.showNotifications(notifications);
                }else{
                    mNotificationView.showEmptyState(true);

                }
            }
        });*/



        realmHelper.EliminarUnMes();

        ArrayList<Notificacion> notifications = showData(filtro);



        //Log.d("Mensajes", nt.toString());

        if (notifications.size() > 0){
            mNotificationView.showEmptyState(false);
            mNotificationView.showNotifications(notifications);
        }else{
            mNotificationView.showEmptyState(true);

        }

    }

    @Override
    public void savePushMessage(int id, String title, String description, Date expireDate, String url, String logo, String categoria, String subcategoria, String url_reagendar, String url_finalizar) {

        Notificacion pushMessage = new Notificacion();
        pushMessage.setId(id);
        pushMessage.setTitulo(title);
        pushMessage.setDescripcion(description);
        pushMessage.setFecha(expireDate);
        pushMessage.setIcono(logo);
        pushMessage.setUrl(url);
        //pushNotification.setId();
        pushMessage.setCategoria(categoria);
        pushMessage.setSubcategoria(subcategoria);
        pushMessage.setUrlReagendar(url_reagendar);
        pushMessage.setUrlFinalizar(url_finalizar);

        PushNotificationsRepository.getInstance().savePushNotification(pushMessage);

        //realmHelper.addNewNotification(title,description,expireDate,url, logo);

        mNotificationView.showEmptyState(false);
        mNotificationView.popPushNotification(pushMessage);
    }

    @Override
    public ArrayList<Notificacion> showData(String filtro) {
        ArrayList<Notificacion> resp = new ArrayList<Notificacion>();
        resp = realmHelper.showNotifications(filtro);
        /*Log.d("obtenidoss", "- "+resp.size());
        for (int i = 0; i < resp.size(); i++)
        {
            Log.d("obtenidos"+i, "- "+resp.get(i).getTitulo()+"-"+resp.get(i).getUrl());
        }*/
        return resp;

    }


}
