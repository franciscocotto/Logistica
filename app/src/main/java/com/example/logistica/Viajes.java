package com.example.logistica;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Viajes {

    private static boolean result;
    private int id_viaje, id_ruta, id_vehiculo, id_conductor;
    private String inicio, finalizacion, nomViaje;

    public Viajes(){}

    public Viajes(int id_viaje, int id_ruta, int id_vehiculo, int id_conductor, String inicio, String finalizacion, String nomViaje) {
        this.id_viaje = id_viaje;
        this.id_ruta = id_ruta;
        this.id_vehiculo = id_vehiculo;
        this.id_conductor = id_conductor;
        this.inicio = inicio;
        this.finalizacion = finalizacion;
        this.nomViaje = nomViaje;
    }

    public static boolean getResult() {
        return result;
    }

    public int getId_viaje() {
        return id_viaje;
    }

    public int getId_ruta() {
        return id_ruta;
    }

    public int getId_vehiculo() {
        return id_vehiculo;
    }

    public int getId_conductor() {
        return id_conductor;
    }

    public String getInicio() {
        return inicio;
    }

    public String getFinalizacion() {
        return finalizacion;
    }

    public String getNomViaje() {
        return nomViaje;
    }


    public static void setResult(boolean result) {
        Viajes.result = result;
    }

    public void setId_viaje(int id_viaje) {
        this.id_viaje = id_viaje;
    }

    public void setId_ruta(int id_ruta) {
        this.id_ruta = id_ruta;
    }

    public void setId_vehiculo(int id_vehiculo) {
        this.id_vehiculo = id_vehiculo;
    }

    public void setId_conductor(int id_conductor) {
        this.id_conductor = id_conductor;
    }

    public void setInicio(String inicio) {
        this.inicio = inicio;
    }

    public void setFinalizacion(String finalizacion) {
        this.finalizacion = finalizacion;
    }

    public void setNomViaje(String nomViaje) {
        this.nomViaje = nomViaje;
    }


    public void cargarViajes(final Context context, String URL, final String accion){
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.DEPRECATED_GET_OR_POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject consulta = new JSONObject(response);

                    String mensaje = null;
                    Viajes.setResult(consulta.getBoolean("error"));
                    mensaje = consulta.getString("message");
                    Toast.makeText(context, mensaje, Toast.LENGTH_SHORT).show();

                }catch (JSONException e){
                    Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                parametros.put("accion",accion);
                parametros.put("id_ruta",String.valueOf(id_ruta));
                parametros.put("id_vehiculo",String.valueOf(id_vehiculo));
                parametros.put("id_conductor",String.valueOf(id_conductor));
                parametros.put("inicio",inicio);
                parametros.put("final",finalizacion);
                parametros.put("nom_viaje",nomViaje);
                return parametros;
            }
        };
        requestQueue.add(stringRequest);
    }
}
