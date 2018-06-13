package com.tecnologiaparainmobiliarias.centrodenotificacionestpi.notificaciones;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.R;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.data.PushNotification;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.model.Notificacion;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

/**
 * Created by guime on 22/03/2018.
 */

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {

    //ArrayList<PushNotification> pushNotifications = new ArrayList<>();
    Context context;
    ArrayList<Notificacion> pushNotifications;
    private OnItemClickListener itemClickListener;


    public NotificationsAdapter(Context context, OnItemClickListener listener, ArrayList<Notificacion> dataNotificacion){
        this.itemClickListener = listener;
        this.pushNotifications = dataNotificacion;
        this.context = context;
    }

    @Override
    public NotificationsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.item_list_notifications, parent, false);

        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(NotificationsAdapter.ViewHolder holder, int position) {

        Notificacion newNotification = pushNotifications.get(position);

        //holder.expiryDate.setText(newNotification.getFecha());

        holder.bind(newNotification.getTitulo(),newNotification.getDescripcion(),newNotification.getFecha(),newNotification.getUrl(), newNotification.getIcono(),newNotification.getCategoria(),newNotification.getSubcategoria(), newNotification.getUrlReagendar(),newNotification.getUrlFinalizar(), itemClickListener);
        //holder.bind(pushNotifications.get(position).getTitulo(),pushNotifications.get(position).getDescripcion(),String.format(pushNotifications.get(position).getFecha()),pushNotifications.get(position).getUrl(), itemClickListener);
    }

    @Override
    public int getItemCount() {
        return pushNotifications.size();
    }

    public void replaceData(ArrayList<Notificacion> items){
        setList(items);
        notifyDataSetChanged();
    }

    public void setList(ArrayList<Notificacion> list){
        this.pushNotifications = list;
    }

    public void addItem(Notificacion pushMessege){
        pushNotifications.add(0,pushMessege);
        notifyItemInserted(0);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView title;
        public TextView description;
        public TextView expiryDate;
        public TextView discount;
        public ImageView iconoView;

        public Button verMas;
        public Button btnFinalizar;
        public Button btnReagendar;

        public ViewHolder(View itemView){
            super(itemView);

            title = (TextView) itemView.findViewById(R.id.tv_title);
            description = (TextView) itemView.findViewById(R.id.tv_description);
            expiryDate = (TextView) itemView.findViewById(R.id.tv_expiry_date);
            //discount = (TextView) itemView.findViewById(R.id.tv_discount);
            iconoView = (ImageView) itemView.findViewById(R.id.tv_image);

            verMas = (Button) itemView.findViewById(R.id.buttonVerMas);
            btnFinalizar = (Button) itemView.findViewById(R.id.buttonFinalizar);
            btnReagendar = (Button) itemView.findViewById(R.id.buttonReagendar);
        }

        public void bind(final String titulo, final String descripcion, final Date fecha, final String url, final String icono, final String categoria, final String subcategoria, final String url_reagendar, final String url_finalizar, final OnItemClickListener listener){
            this.title.setText(titulo);
            this.description.setText(descripcion);
            //this.expiryDate.setText(fecha.toString());
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
            String fechaM = format.format(fecha);


            this.expiryDate.setText(fechaM);
            if(icono != null){

                Picasso.with(context).load(icono).resize(150,150).transform(new CropCircleTransformation()).centerCrop().into(iconoView);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(url, getAdapterPosition());
                }
            });

            verMas.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(url, getAdapterPosition());
                }
            });



            if(categoria.equals("OTROS")){

                btnReagendar.setVisibility(View.GONE);
                btnFinalizar.setVisibility(View.GONE);

                if(subcategoria.equals("agenda_compromisos_y_tareas_del_dia")){

                    if(url_reagendar != null){

                        btnReagendar.setVisibility(View.VISIBLE);
                        btnReagendar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                listener.onItemClick(url_reagendar,getAdapterPosition());
                            }
                        });
                    }
                    if (url_finalizar != null){

                        btnFinalizar.setVisibility(View.VISIBLE);
                        btnFinalizar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                listener.onItemClick(url_finalizar,getAdapterPosition());
                            }
                        });
                    }
                }
            }else{

                btnFinalizar.setVisibility(View.GONE);
                btnReagendar.setVisibility(View.GONE);
            }
        }
    }

    public interface OnItemClickListener{
        void onItemClick(String url, int position);

    }
}
