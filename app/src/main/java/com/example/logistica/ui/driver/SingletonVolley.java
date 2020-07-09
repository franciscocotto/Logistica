package com.example.logistica.ui.driver;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.facebook.internal.ImageRequest;

/**
 * Created by CHENAO on 19/11/2017.
 */

public class SingletonVolley {

    private static SingletonVolley intanciaVolley;
    private RequestQueue request;
    private static Context contexto;

    private SingletonVolley(Context context) {
        contexto = context;
        request = getRequestQueue();
    }


    public static synchronized SingletonVolley getIntanciaVolley(Context context) {
        if (intanciaVolley == null) {
            intanciaVolley = new SingletonVolley(context);
        }

        return intanciaVolley;
    }

    public RequestQueue getRequestQueue() {
        if (request == null) {
            request = Volley.newRequestQueue(contexto.getApplicationContext());
        }

        return request;
    }

    public <T> void addToRequestQueue(Request<T> request) {
        getRequestQueue().add(request);
    }

    public void addToRequestQueue(ImageRequest imageRequest) {
    }
}
