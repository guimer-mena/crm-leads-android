package com.tecnologiaparainmobiliarias.centrodenotificacionestpi.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.firebase.messaging.RemoteMessage;
import com.squareup.picasso.Picasso;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.MainActivity;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.R;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.notificaciones.NotificationsFragment;

import java.io.IOException;
import java.util.Map;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

import static android.content.ContentValues.TAG;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private static final String TAG = FirebaseMessagingService.class.getSimpleName();

    public FirebaseMessagingService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "¡Mensaje recibido!");
        displayNotification(remoteMessage.getNotification(), remoteMessage.getData());
        sendNewPromoBroadcast(remoteMessage);
    }

    private void sendNewPromoBroadcast(RemoteMessage remoteMessage) {
        Intent intent = new Intent(NotificationsFragment.ACTION_NOTIFY_NEW_NOTIFY);
        intent.putExtra("titulo", remoteMessage.getNotification().getTitle());
        intent.putExtra("descripcion", remoteMessage.getNotification().getBody());
        intent.putExtra("fecha", remoteMessage.getData().get("fecha"));
        intent.putExtra( "url", remoteMessage.getData().get("url"));
        intent.putExtra("logo", remoteMessage.getData().get("icono"));
        //intent.putExtra("discount", remoteMessage.getData().get("discount"));
        LocalBroadcastManager.getInstance(getApplicationContext())
                .sendBroadcast(intent);
    }

    private void displayNotification(RemoteMessage.Notification notification, Map<String, String> data) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);



        //final RemoteViews.RemoteView remoteView = new RemoteViews.RemoteView(getPackageName(),R.layout.re)

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ico_crm_color)
                .setContentTitle(notification.getTitle())
                .setContentText(notification.getBody())
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(pendingIntent);

        String picture = data.get("icono");
        Bitmap bmp = null;
        try {
            bmp = Picasso.with(getApplicationContext()).load(picture).resize(400, 400).transform(new CropCircleTransformation()).centerCrop().get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(bmp != null){
            notificationBuilder.setLargeIcon(bmp);
        }

        /*if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            notificationBuilder.setColor(Color.GREEN);
        }*/

        /*if(data.get("icono") != ""){
            notificationBuilder.setLargeIcon();
        }
        if(data.get("url") != ""){

        }*/

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
}
