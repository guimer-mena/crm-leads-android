package com.tecnologiaparainmobiliarias.centrodenotificacionestpi.model;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.ResponseBody;

/**
 * Created by guime on 22/03/2018.
 */

public class ApiError {
    private String estado;

    public ApiError(String estado){
        this.estado = estado;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public static ApiError fromResponseBody(ResponseBody responseBody) {
        Gson gson = new Gson();
        try {
            return gson.fromJson(responseBody.string(), ApiError.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
