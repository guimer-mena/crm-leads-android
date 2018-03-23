package com.tecnologiaparainmobiliarias.centrodenotificacionestpi.notificaciones;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.R;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.data.PushNotification;

import java.util.ArrayList;

/**
 * create an instance of this fragment.
 */
public class NotificationsFragment extends Fragment implements NotificationsContract.View {

    public static final String ACTION_NOTIFY_NEW_PROMO = "NOTIFY_NEW_PROMO";
    private BroadcastReceiver mNotificacionReceiver;

    private RecyclerView mRecyclerView;
    private LinearLayout mNoMessagesView;
    private NotificationsAdapter mNotificationAdapter;

    private NotificationsPresenter mPresenter;

    public NotificationsFragment() {
        // Required empty public constructor
    }

    public static NotificationsFragment newInstance(){
        NotificationsFragment fragment = new NotificationsFragment();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
        mNotificacionReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String titulo = intent.getStringExtra("titulo");
                String descripcion = intent.getStringExtra("descripcion");
                String fecha = intent.getStringExtra("fecha");
                String url = intent.getStringExtra("url");

                mPresenter.savePushMessage(titulo,descripcion,fecha,url);
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_notifications,container,false);

        mNotificationAdapter = new NotificationsAdapter();
        mRecyclerView = (RecyclerView) root.findViewById(R.id.rv_notifications_list);
        mNoMessagesView = (LinearLayout) root.findViewById(R.id.noMessages);
        mRecyclerView.setAdapter(mNotificationAdapter);

        return root;
    }

    @Override
    public void onResume(){
        super.onResume();

        mPresenter.start();

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mNotificacionReceiver, new IntentFilter(ACTION_NOTIFY_NEW_PROMO));
    }

    @Override
    public void onPause(){
        super.onPause();

        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mNotificacionReceiver);
    }

    @Override
    public void setPresenter(NotificationsContract.Presenter presenter) {
        if(presenter != null){
            mPresenter = (NotificationsPresenter) presenter;
        }else{
            throw new RuntimeException("El presenter de notifcaciones no puede ser null");
        }
    }

    @Override
    public void showNotifications(ArrayList<PushNotification> notifications) {
        mNotificationAdapter.replaceData(notifications);
    }

    @Override
    public void showEmptyState(boolean empty) {
        mRecyclerView.setVisibility(empty ? View.GONE: View.VISIBLE);
        mNoMessagesView.setVisibility(empty ? View.VISIBLE : View.GONE);
    }

    @Override
    public void popPushNotification(PushNotification pushMessage) {
        mNotificationAdapter.addItem(pushMessage);
    }

}