package com.tecnologiaparainmobiliarias.centrodenotificacionestpi.data;

import android.support.v4.util.ArrayMap;

import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.model.Notificacion;

import java.util.ArrayList;

/**
 * Created by guime on 22/03/2018.
 */

public class PushNotificationsRepository {
    private static ArrayMap<Integer, Notificacion> LOCAL_PUSH_NOTIFICATIONS = new ArrayMap<>();
    private static PushNotificationsRepository INSTANCE;

    private PushNotificationsRepository() {
    }

    public static PushNotificationsRepository getInstance() {
        if (INSTANCE == null) {
            return new PushNotificationsRepository();
        } else {
            return INSTANCE;
        }
    }

    public void getPushNotifications(LoadCallback callback) {
        callback.onLoaded(new ArrayList<>(LOCAL_PUSH_NOTIFICATIONS.values()));
    }

    public void savePushNotification(Notificacion notification) {
        LOCAL_PUSH_NOTIFICATIONS.put(notification.getId(), notification);
    }

    public interface LoadCallback {
        void onLoaded(ArrayList<Notificacion> notifications);
    }
}
