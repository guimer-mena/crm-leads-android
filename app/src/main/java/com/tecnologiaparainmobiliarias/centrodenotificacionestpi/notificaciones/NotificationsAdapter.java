package com.tecnologiaparainmobiliarias.centrodenotificacionestpi.notificaciones;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.R;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.data.PushNotification;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.model.Notificacion;

import java.util.ArrayList;

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
        //PushNotification newNotification = pushNotifications.get(position);
        Notificacion newNotification = pushNotifications.get(position);

        //Log.d("bindHolder", "-"+pushNotifications.get(position).getTitulo()+pushNotifications.get(position).getDescripcion()+String.format(pushNotifications.get(position).getFecha())+pushNotifications.get(position).getUrl()+ pushNotifications.get(position).getIcono());

        //holder.title.setText(newNotification.getTitulo());
        //holder.description.setText(newNotification.getDescripcion());
        //holder.expiryDate.setText(String.format(newNotification.getFecha()));
        //holder.expiryDate.setText(newNotification.getFecha());

        holder.bind(newNotification.getTitulo(),newNotification.getDescripcion(),newNotification.getFecha(),newNotification.getUrl(), newNotification.getIcono(), itemClickListener);
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

        public ViewHolder(View itemView){
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tv_title);
            description = (TextView) itemView.findViewById(R.id.tv_description);
            expiryDate = (TextView) itemView.findViewById(R.id.tv_expiry_date);
            //discount = (TextView) itemView.findViewById(R.id.tv_discount);
            iconoView = (ImageView) itemView.findViewById(R.id.tv_image);
        }

        public void bind(final String titulo, final String descripcion, final String fecha, final String url, final String icono, final OnItemClickListener listener){
            this.title.setText(titulo);
            this.description.setText(descripcion);
            this.expiryDate.setText(fecha);
            if(icono != null){

                Picasso.with(context).load(icono).resize(150,150).transform(new CropCircleTransformation()).centerCrop().into(iconoView);
            }

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(url, getAdapterPosition());
                }
            });
        }
    }

    public interface OnItemClickListener{
        void onItemClick(String url, int position);
    }
}
