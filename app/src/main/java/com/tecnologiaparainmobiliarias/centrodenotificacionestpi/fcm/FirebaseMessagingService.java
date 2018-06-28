package com.tecnologiaparainmobiliarias.centrodenotificacionestpi.fcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.firebase.messaging.RemoteMessage;
import com.squareup.picasso.Picasso;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.MainActivity;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.R;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.model.Realm.RealmHelper;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.notificaciones.NotificationsFragment;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

import static android.content.ContentValues.TAG;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private static final String TAG = FirebaseMessagingService.class.getSimpleName();
    private RealmHelper realmHelper;

    public FirebaseMessagingService() {
    }


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "¡Mensaje recibido!");

        //displayNotification(remoteMessage.getNotification(), remoteMessage.getData());

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        if(remoteMessage.getData().get("categoria").toString() != ""){
            Boolean Mostrar = true;
            String clave = remoteMessage.getData().get("subcategoria").toString();
            clave = "pref_notificacion_"+clave;
            if(sharedPrefs.getBoolean(clave, true)){
                displayNotification2(remoteMessage.getData());
            }
        }

        //displayNotification2(remoteMessage.getData());


        realmHelper = new RealmHelper(getApplicationContext());

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date fecha = null;
        try {
            fecha = format.parse(remoteMessage.getData().get("fecha"));

        } catch (ParseException e) {
            e.printStackTrace();
        }

        realmHelper.addNewNotification(remoteMessage.getData().get("title"),remoteMessage.getData().get("body"),fecha,remoteMessage.getData().get("url"), remoteMessage.getData().get("icono"), remoteMessage.getData().get("categoria"), remoteMessage.getData().get("subcategoria"), remoteMessage.getData().get("url_reagendar"), remoteMessage.getData().get("url_finalizar"),"NO");
        int idNuevo = realmHelper.obtenerUltimoId();
        
        sendNewPromoBroadcast(remoteMessage, idNuevo);
    }

    private void sendNewPromoBroadcast(RemoteMessage remoteMessage, int id) {
        Intent intent = new Intent(NotificationsFragment.ACTION_NOTIFY_NEW_NOTIFY);
        //intent.putExtra("titulo", remoteMessage.getNotification().getTitle());
        //intent.putExtra("descripcion", remoteMessage.getNotification().getBody());

        intent.putExtra("id",id);
        intent.putExtra("titulo", remoteMessage.getData().get("title"));
        intent.putExtra("descripcion", remoteMessage.getData().get("body"));
        intent.putExtra("fecha", remoteMessage.getData().get("fecha"));
        intent.putExtra( "url", remoteMessage.getData().get("url"));
        intent.putExtra("logo", remoteMessage.getData().get("icono"));
        intent.putExtra("categoria", remoteMessage.getData().get("categoria"));
        intent.putExtra("subcategoria", remoteMessage.getData().get("subcategoria"));
        intent.putExtra("url_reagendar", remoteMessage.getData().get("url_reagendar"));
        intent.putExtra("url_finalizar", remoteMessage.getData().get("url_finalizar"));
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


        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0, notificationBuilder.build());
    }
    private void displayNotification2(Map<String, String> data) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);



        //final RemoteViews.RemoteView remoteView = new RemoteViews.RemoteView(getPackageName(),R.layout.re)

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ico_crm_color)
                .setContentTitle(data.get("title"))
                .setContentText(data.get("body"))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(pendingIntent).setPriority(1);

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

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            notificationBuilder.setColor(Color.rgb(21,157,215));
        }

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
