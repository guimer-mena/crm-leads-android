package com.tecnologiaparainmobiliarias.centrodenotificacionestpi.notificaciones;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.CrmApi;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.MainActivity;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.R;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.RecyclerItemTouchHelper;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.data.PushNotification;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.model.Notificacion;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.model.Realm.RealmHelper;
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
public class NotificationsFragment extends Fragment implements NotificationsContract.View, RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    public static final String ACTION_NOTIFY_NEW_NOTIFY = "NEW_NOTIFY";

    public String Filtro = "todos";

    private BroadcastReceiver mNotificacionReceiver;

    private RecyclerView mRecyclerView;
    private RelativeLayout mNoMessagesView;
    private NotificationsAdapter mNotificationAdapter;

    private NotificationsPresenter mPresenter;

    private Retrofit mRestAdaper;
    private CrmApi mCrmApi;

    private CoordinatorLayout coordinatorLayout;
    private ArrayList<Notificacion> arrayNotificaciones;

    private RealmHelper realmHelper;

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

        realmHelper = new RealmHelper(getContext());

        //Crear conexion REST
        mRestAdaper = new Retrofit.Builder().baseUrl(CrmApi.BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        //Crear conexion
        mCrmApi = mRestAdaper.create(CrmApi.class);

        arrayNotificaciones = new ArrayList<>();

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
                int id = intent.getIntExtra("id",0);
                String url = intent.getStringExtra("url");
                String logo = intent.getStringExtra("logo");
                String categoria = intent.getStringExtra("categoria");
                String subcategoria = intent.getStringExtra("subcategoria");
                String url_reagendar = intent.getStringExtra("url_reagendar");
                String url_finalizar = intent.getStringExtra("url_finalizar");

                mPresenter.savePushMessage(id,titulo,descripcion,fecha,url,logo, categoria, subcategoria, url_reagendar, url_finalizar);
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_notifications,container,false);

        coordinatorLayout = root.findViewById(R.id.coordinator_layout);

        mNotificationAdapter = new NotificationsAdapter(getActivity().getApplicationContext(), new NotificationsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(String url, int position) {

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

        }, mPresenter.showData("todos"));
        mRecyclerView = (RecyclerView) root.findViewById(R.id.rv_notifications_list);
        mNoMessagesView = (RelativeLayout) root.findViewById(R.id.noMessages);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());

        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mRecyclerView.setAdapter(mNotificationAdapter);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mRecyclerView);


        ItemTouchHelper.SimpleCallback itemTouchHelperCallback1 = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT ) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                // Row is swiped from recycler view
                // remove it from adapter
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        // attaching the touch helper to recycler view
        new ItemTouchHelper(itemTouchHelperCallback1).attachToRecyclerView(mRecyclerView);

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

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {

        if(viewHolder instanceof NotificationsAdapter.ViewHolder){
            arrayNotificaciones = mNotificationAdapter.pushNotifications;
            String nombre = arrayNotificaciones.get(viewHolder.getAdapterPosition()).getTitulo();
            final int idEliminar = arrayNotificaciones.get(viewHolder.getAdapterPosition()).getId();

            final Notificacion itemEliminado = arrayNotificaciones.get(viewHolder.getAdapterPosition());
            final int posicionEliminado = viewHolder.getAdapterPosition();

            mNotificationAdapter.RemoverNotificacion(position);

            final int Restantes = mNotificationAdapter.getItemCount();
            if(Restantes == 0){
                showEmptyState(true);
            }

            final boolean[] eliminarBase = {true};
            Snackbar snackbar = Snackbar
                    .make(getActivity().findViewById(R.id.coordinator_layout), nombre + " eliminado!", Snackbar.LENGTH_LONG);
            snackbar.setAction("Deshacer", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(Restantes == 0){
                        showEmptyState(false);
                    }
                    eliminarBase[0] = false;
                    mNotificationAdapter.RestoreItem(itemEliminado,posicionEliminado);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
            snackbar.addCallback(new Snackbar.Callback(){
               @Override
               public void onDismissed(Snackbar snackbar, int event) {
                   if(eliminarBase[0] == true){
                       realmHelper.deleteNotification(idEliminar);
                   }
               }
            });
        }
    }
}
