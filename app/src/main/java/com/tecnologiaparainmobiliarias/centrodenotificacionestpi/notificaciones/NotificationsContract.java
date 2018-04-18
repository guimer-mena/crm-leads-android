package com.tecnologiaparainmobiliarias.centrodenotificacionestpi.notificaciones;

import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.BasePresenter;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.BaseView;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.data.PushNotification;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.model.Notificacion;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by guime on 22/03/2018.
 */

public interface NotificationsContract {
    interface View extends BaseView<Presenter>{
        void showNotifications(ArrayList<Notificacion> notifications);
        void showEmptyState(boolean empty);
        void popPushNotification(Notificacion pushMessage);
    }

    interface Presenter extends BasePresenter{
        void registerAppClient();
        void loadNotifications();
        void savePushMessage(String title, String description, Date expireDate, String url, String logo);
        ArrayList<Notificacion> showData();
    }
}
