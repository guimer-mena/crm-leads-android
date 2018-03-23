package com.tecnologiaparainmobiliarias.centrodenotificacionestpi.notificaciones;

import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.BasePresenter;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.BaseView;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.data.PushNotification;

import java.util.ArrayList;

/**
 * Created by guime on 22/03/2018.
 */

public interface NotificationsContract {
    interface View extends BaseView<Presenter>{
        void showNotifications(ArrayList<PushNotification> notifications);
        void showEmptyState(boolean empty);
        void popPushNotification(PushNotification pushMessage);
    }

    interface Presenter extends BasePresenter{
        void registerAppClient();
        void loadNotifications();
        void savePushMessage(String title, String description,String expireDate, String discount);
    }
}
