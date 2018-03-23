package com.tecnologiaparainmobiliarias.centrodenotificacionestpi.notificaciones;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.R;
import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.data.PushNotification;

import java.util.ArrayList;

/**
 * Created by guime on 22/03/2018.
 */

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {

    ArrayList<PushNotification> pushNotifications = new ArrayList<>();

    public NotificationsAdapter(){

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
        PushNotification newNotification = pushNotifications.get(position);

        holder.title.setText(newNotification.getTitulo());
        holder.description.setText(newNotification.getDescripcion());
        holder.expiryDate.setText(String.format(newNotification.getFecha()));
    }

    @Override
    public int getItemCount() {
        return pushNotifications.size();
    }

    public void replaceData(ArrayList<PushNotification> items){
        setList(items);
        notifyDataSetChanged();
    }

    public void setList(ArrayList<PushNotification> list){
        this.pushNotifications = list;
    }

    public void addItem(PushNotification pushMessege){
        pushNotifications.add(0,pushMessege);
        notifyItemInserted(0);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView title;
        public TextView description;
        public TextView expiryDate;
        public TextView discount;

        public ViewHolder(View itemView){
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tv_title);
            description = (TextView) itemView.findViewById(R.id.tv_description);
            expiryDate = (TextView) itemView.findViewById(R.id.tv_expiry_date);
            //discount = (TextView) itemView.findViewById(R.id.tv_discount);
        }
    }
}
