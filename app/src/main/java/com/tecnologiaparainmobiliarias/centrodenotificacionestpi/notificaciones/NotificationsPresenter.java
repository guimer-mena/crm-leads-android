package com.tecnologiaparainmobiliarias.centrodenotificacionestpi.notificaciones;

import android.content.Context;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessaging;
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
        loadNotifications();
    }

    @Override
    public void registerAppClient() {
        mFCMInteractor.subscribeToTopic("promos");
    }

    @Override
    public void loadNotifications() {

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


        ArrayList<Notificacion> notifications = showData();

        if (notifications.size() > 0){
            mNotificationView.showEmptyState(false);
            mNotificationView.showNotifications(notifications);
        }else{
            mNotificationView.showEmptyState(true);

        }

    }

    @Override
    public void savePushMessage(String title, String description, Date expireDate, String url, String logo) {
        Notificacion pushMessage = new Notificacion();
        pushMessage.setTitulo(title);
        pushMessage.setDescripcion(description);
        pushMessage.setFecha(expireDate);
        pushMessage.setIcono(logo);
        pushMessage.setUrl(url);
        //pushNotification.setId();

        PushNotificationsRepository.getInstance().savePushNotification(pushMessage);
        //Log.d("DatosGusrdar", "-"+title+"-"+description+"-"+expireDate+url+"-"+logo);
        realmHelper.addNewNotification(title,description,expireDate,url, logo);

        mNotificationView.showEmptyState(false);
        mNotificationView.popPushNotification(pushMessage);
    }

    @Override
    public ArrayList<Notificacion> showData() {
        ArrayList<Notificacion> resp = new ArrayList<Notificacion>();
        resp = realmHelper.showAllNotifications();
        /*Log.d("obtenidoss", "- "+resp.size());
        for (int i = 0; i < resp.size(); i++)
        {
            Log.d("obtenidos"+i, "- "+resp.get(i).getTitulo()+"-"+resp.get(i).getUrl());
        }*/
        return resp;

    }


}
