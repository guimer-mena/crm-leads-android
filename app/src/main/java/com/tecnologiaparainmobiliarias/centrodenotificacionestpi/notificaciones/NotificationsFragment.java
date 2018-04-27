package com.tecnologiaparainmobiliarias.centrodenotificacionestpi.notificaciones;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.CrmApi;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.MainActivity;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.R;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.data.PushNotification;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.model.Notificacion;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.model.Retrofit.ObtenerTokenActivo;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import io.realm.Realm;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * create an instance of this fragment.
 */
public class NotificationsFragment extends Fragment implements NotificationsContract.View {

    public static final String ACTION_NOTIFY_NEW_NOTIFY = "NEW_NOTIFY";
    private BroadcastReceiver mNotificacionReceiver;

    private RecyclerView mRecyclerView;
    private LinearLayout mNoMessagesView;
    private NotificationsAdapter mNotificationAdapter;

    private NotificationsPresenter mPresenter;
    private Realm realm;

    private Retrofit mRestAdaper;
    private CrmApi mCrmApi;

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

        //Crear conexion REST
        mRestAdaper = new Retrofit.Builder().baseUrl(CrmApi.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        //Crear conexion
        mCrmApi = mRestAdaper.create(CrmApi.class);




        realm = Realm.getDefaultInstance();

        mNotificacionReceiver = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {
                String titulo = intent.getStringExtra("titulo");
                String descripcion = intent.getStringExtra("descripcion");
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date fecha = null;
                try {
                    fecha = format.parse(intent.getStringExtra("fecha"));

                } catch (ParseException e) {
                    e.printStackTrace();
                    //Log.d("FECHA",e.toString());
                }
                String url = intent.getStringExtra("url");
                String logo = intent.getStringExtra("logo");
                //Log.d("URLNOTIFICACION", "- "+url);
                //Log.d("URLNOTIFICACION", "- "+logo+" - "+descripcion+" - "+fecha+" - "+url+" - "+titulo+" - ");
                mPresenter.savePushMessage(titulo,descripcion,fecha,url,logo);
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_notifications,container,false);

        mNotificationAdapter = new NotificationsAdapter(getActivity().getApplicationContext(), new NotificationsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String url, int position) {
                //Toast.makeText( getActivity() , url +" - "+ position, Toast.LENGTH_LONG).show();
                if(url != null && !url.isEmpty()){

                    final SharedPreferences mPref = getActivity().getSharedPreferences("USER_CRM", Context.MODE_PRIVATE);
                    final String[] claveSession = {mPref.getString("PREF_CLAVE_SESION", "")};
                    String idusuario = mPref.getString("PREF_ID", "");
                    String cuenta = mPref.getString("PREF_CUENTA", "");
                    String iddispositivo = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);

                    Call<ObtenerTokenActivo> obtenerTokenActivo = mCrmApi.obtenerTokenActivo(cuenta, claveSession[0],idusuario,"APP_ANDROID",iddispositivo);

                    obtenerTokenActivo.enqueue(new Callback<ObtenerTokenActivo>() {
                        @Override
                        public void onResponse(Call<ObtenerTokenActivo> call, Response<ObtenerTokenActivo> response) {
                            if (response.isSuccessful()) {
                                //Log.d("NUEVA_CLAVE",response.body().getClave_session());
                                if(!response.body().getClave_session().isEmpty()){
                                    String claveActual = claveSession[0];
                                    if(response.body().getClave_session() != claveActual ){
                                        SharedPreferences.Editor editor = mPref.edit();
                                        editor.putString("PREF_CLAVE_SESION", response.body().getClave_session());

                                    }

                                }

                            }
                            claveSession[0] = response.body().getClave_session().toString();
                        }

                        @Override
                        public void onFailure(Call<ObtenerTokenActivo> call, Throwable t) {

                        }
                    });

                    String urlNueva = "http://10.0.2.2:8007/acceso/acceso_app.php?numeroCuenta="+cuenta+"&claveSession="+ claveSession[0] +"&url="+url;
                    String usls = Uri.encode(url);
                    //Log.d("NUEVA_URL", usls);
                    Toast.makeText(getContext(),urlNueva+usls,Toast.LENGTH_LONG).show();
                    Intent intentWeb = new Intent(Intent.ACTION_VIEW, Uri.parse(urlNueva));
                    startActivity(intentWeb);
                }
            }

        }, mPresenter.showData());
        mRecyclerView = (RecyclerView) root.findViewById(R.id.rv_notifications_list);
        mNoMessagesView = (LinearLayout) root.findViewById(R.id.noMessages);
        mRecyclerView.setAdapter(mNotificationAdapter);

        return root;
    }

    @Override
    public void onResume(){
        super.onResume();

        mPresenter.start();

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mNotificacionReceiver, new IntentFilter(ACTION_NOTIFY_NEW_NOTIFY));
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
    public void showNotifications(ArrayList<Notificacion> notifications) {
        mNotificationAdapter.replaceData(notifications);
    }

    @Override
    public void showEmptyState(boolean empty) {
        mRecyclerView.setVisibility(empty ? View.GONE: View.VISIBLE);
        mNoMessagesView.setVisibility(empty ? View.VISIBLE : View.GONE);
    }

    @Override
    public void popPushNotification(Notificacion pushMessage) {
        mNotificationAdapter.addItem(pushMessage);
    }

}
