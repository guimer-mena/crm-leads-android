package com.tecnologiaparainmobiliarias.centrodenotificacionestpi.model.Realm;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.tecnologiaparainmobiliarias.centrodenotificacionestpi.model.Notificacion;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;

public class RealmHelper {
    private Realm realm;
    private Context context;
    private RealmResults<Notificacion> realmResults;

    public static AtomicInteger NotificacionId = new AtomicInteger();

    public RealmHelper(Context context){
        Realm.init(context);
        this.context = context;
        RealmConfiguration config = new RealmConfiguration.Builder().deleteRealmIfMigrationNeeded().build();
        realm = Realm.getInstance(config);

        this.NotificacionId = getIdByTable(realm, Notificacion.class);

    }

    private int getCount(){
        realmResults = realm.where(Notificacion.class).findAll();
        realmResults.sort("Id");
        return realmResults.size();
    }

    public ArrayList<Notificacion> showAllNotifications(){
        int id;
        String titulo;
        String descripcion;
        Date fecha;
        String url;
        String icono;
        String categoria;
        String subcategoria;
        String visto;
        String urlReagendar;
        String urlFinalizar;

        ArrayList<Notificacion> data = new ArrayList<>();

        realmResults = realm.where(Notificacion.class).findAll();
        realmResults = realmResults.sort("Id", Sort.DESCENDING);

        if(realmResults.size() > 0){
            for (int i = 0; i < realmResults.size(); i++){
                //Log.d("IdNotificacion"+i, ". "+realmResults.get(i).getId());
                titulo = realmResults.get(i).getTitulo();
                descripcion = realmResults.get(i).getDescripcion();
                fecha = realmResults.get(i).getFecha();
                url = realmResults.get(i).getUrl();
                icono = realmResults.get(i).getIcono();
                visto = realmResults.get(i).getVisto();
                categoria = realmResults.get(i).getCategoria();
                subcategoria = realmResults.get(i).getSubcategoria();
                urlFinalizar = realmResults.get(i).getUrlFinalizar();
                urlReagendar = realmResults.get(i).getUrlReagendar();


                data.add( new Notificacion(titulo, descripcion,url,icono,fecha,categoria,subcategoria,urlReagendar,urlFinalizar,visto));

            }
        }

        return data;
    }

    public ArrayList<Notificacion> showNotifications(String filtro){
        int id;
        String titulo;
        String descripcion;
        Date fecha;
        String url;
        String icono;
        String categoria;
        String subcategoria;
        String visto;
        String urlReagendar;
        String urlFinalizar;

        String filtroQuery = "";

        switch (filtro){
            case "todos":
                filtroQuery = "TODOS";
                break;
            case "sistema":
                filtroQuery = "AVISOS_DEL_SISTEMA";
                break;
            case "leads":
                filtroQuery = "LEADS";
                break;
            case "bolsa24siete":
                filtroQuery = "BOLSA24SIETE";
                break;
            case "otros":
                filtroQuery = "OTROS";
                break;
            default:
                filtroQuery = "TODOS";
                break;
        }


        ArrayList<Notificacion> data = new ArrayList<>();

        if(filtroQuery.equals("TODOS")) {
            realmResults = realm.where(Notificacion.class).findAll();
        }else {
            realmResults = realm.where(Notificacion.class).equalTo("Categoria",filtroQuery).findAll();
        }

        realmResults = realmResults.sort("Id", Sort.DESCENDING);

        if(realmResults.size() > 0){
            for (int i = 0; i < realmResults.size(); i++){
                //Log.d("IdNotificacion"+i, ". "+realmResults.get(i).getId());
                id = realmResults.get(i).getId();
                titulo = realmResults.get(i).getTitulo();
                descripcion = realmResults.get(i).getDescripcion();
                fecha = realmResults.get(i).getFecha();
                url = realmResults.get(i).getUrl();
                icono = realmResults.get(i).getIcono();
                visto = realmResults.get(i).getVisto();
                categoria = realmResults.get(i).getCategoria();
                subcategoria = realmResults.get(i).getSubcategoria();
                urlFinalizar = realmResults.get(i).getUrlFinalizar();
                urlReagendar = realmResults.get(i).getUrlReagendar();

                Notificacion not = new Notificacion(titulo, descripcion,url,icono,fecha,categoria,subcategoria,urlReagendar,urlFinalizar,visto);

                not.setId(id);

                data.add( not);

            }
        }

        return data;
    }

    public int obtenerUltimoId(){

        Number id = realm.where(Notificacion.class).max("Id");

        return id.intValue();
    }

    public void EliminarUnMes(){
        //Obtenemos fecha actual
        Date fechaActual = new Date();

        //Sumamos un mes
        Calendar cal = Calendar.getInstance();
        cal.setTime(fechaActual);
        cal.add(Calendar.MONTH, 1);

        //Convertimis a nueva fecha
        Date nuevaFecha = cal.getTime();

        RealmResults<Notificacion> dataRealmDelete = realm.where(Notificacion.class).greaterThan("Fecha", nuevaFecha  ).findAll();

        //Eliminadmos todas la coincidencias
        realm.beginTransaction();
        dataRealmDelete.deleteAllFromRealm();
        realm.commitTransaction();

    }

    public void addNewNotification(String name, String descipcion, Date fecha, String url, String logo, String categoria, String subcategoria, String urlReagendar, String urlFinaliar, String visto) {
        Notificacion data = new Notificacion();

        //data.setId(getCount() + 1);
        data.setId(this.NotificacionId.incrementAndGet());
        //Log.d("IdInicio3: ","- "+this.NotificacionId.incrementAndGet());
        data.setTitulo(name);
        data.setDescripcion(descipcion);
        data.setUrl(url);
        data.setDescripcionComleta(descipcion);
        data.setFecha(fecha);
        data.setIcono(logo);
        data.setVisto(visto);
        data.setCategoria(categoria);
        data.setSubcategoria(subcategoria);
        data.setUrlFinalizar(urlFinaliar);
        data.setUrlReagendar(urlReagendar);


        realm.beginTransaction();
        realm.copyToRealm(data);
        realm.commitTransaction();
        //Log.d("Guardar_Notificacion", "Notificacion Guardada: "+name+" / "+descipcion+" / "+fecha+" / "+ url + " / "+logo);
    }
    public void deleteNotification(int id){
        RealmResults<Notificacion> dataRealm = realm.where(Notificacion.class).equalTo("Id", id).findAll();
        realm.beginTransaction();
        //dataRealm.remove(0);
        //dataRealm.clear();
        dataRealm.deleteAllFromRealm();
        realm.commitTransaction();

    }

    private <T extends RealmObject> AtomicInteger getIdByTable(Realm realm, Class<T> anyClass){

        RealmResults<T> results = realm.where(anyClass).findAll();
        return (results.size() > 0 )? new AtomicInteger(results.max("Id").intValue()) : new AtomicInteger();

    }
}
