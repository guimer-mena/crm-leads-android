package com.tecnologiaparainmobiliarias.centrodenotificacionestpi.notificaciones;

import com.google.firebase.messaging.FirebaseMessaging;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.data.PushNotification;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.data.PushNotificationsRepository;

import java.util.ArrayList;

/**
 * Created by guime on 22/03/2018.
 */

public class NotificationsPresenter implements NotificationsContract.Presenter {

    private final NotificationsContract.View mNotificationView;
    private final FirebaseMessaging mFCMInteractor;

    public NotificationsPresenter(NotificationsContract.View mNotificationView, FirebaseMessaging mFCMInteractor) {
        this.mNotificationView = mNotificationView;
        this.mFCMInteractor = mFCMInteractor;

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

        PushNotificationsRepository.getInstance().getPushNotifications(new PushNotificationsRepository.LoadCallback() {
            @Override
            public void onLoaded(ArrayList<PushNotification> notifications) {
                if (notifications.size() > 0){
                    mNotificationView.showEmptyState(false);
                    mNotificationView.showNotifications(notifications);
                }else{
                    mNotificationView.showEmptyState(true);

                }
            }
        });

    }

    @Override
    public void savePushMessage(String title, String description, String expireDate, String discount) {
        PushNotification pushMessage = new PushNotification();
        pushMessage.setTitulo(title);
        pushMessage.setDescripcion(description);
        pushMessage.setFecha(expireDate);
        //pushNotification.setId();

        PushNotificationsRepository.getInstance().savePushNotification(pushMessage);

        mNotificationView.showEmptyState(false);
        mNotificationView.popPushNotification(pushMessage);
    }
}
