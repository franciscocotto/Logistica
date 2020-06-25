package com.example.logistica.ui.ruta;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.logistica.Administrador;
import com.example.logistica.Despachador;
import com.example.logistica.MainActivity;
import com.example.logistica.MapsActivity;
import com.example.logistica.R;
import com.example.logistica.SharedPrefManager;
import com.example.logistica.User;
import com.example.logistica.Utilidades;
import com.example.logistica.VolleySingleton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.ClientProtocolException;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;

public class addRuta extends Fragment {

    private GoogleMap mMap;
    ProgressDialog pDialog;
    EditText txtLatInicio,txtLongInicio,txtLatFinal,txtLongFinal, addnameRuta, txtorigen, txtdestino;

    JsonObjectRequest jsonObjectRequest;
    RequestQueue request;

    public addRuta() {

    }
    String url_guardar = "https://inventario-pdm115.000webhostapp.com/Logistica/ws_ca06025/PostRuta.php";
    ProgressBar progressBar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_ruta, container, false);
        addnameRuta = (EditText) view.findViewById(R.id.addnameRuta);
        txtorigen= (EditText) view.findViewById(R.id.origen);
        txtdestino= (EditText) view.findViewById(R.id.destino);
        Button get = (Button) view.findViewById(R.id.btnDocumento);
        Button addMapas = (Button) view.findViewById(R.id.addMappas);
        Button clean = (Button) view.findViewById(R.id.clean);
        clean.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addnameRuta.setText("");
                txtorigen.setText("");
                txtdestino.setText("");

            }
        });

        addMapas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String origen = txtorigen.getText().toString().trim();
                final String destino = txtdestino.getText().toString().trim();
                if (TextUtils.isEmpty(origen)) {
                    txtLatInicio.setError("Favor Ingresar Origen de la Ruta");
                    txtLatInicio.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(destino)) {
                    txtLatInicio.setError("Favor Ingresar Destino de la Ruta");
                    txtLatInicio.requestFocus();
                    return;
                }
                pDialog = new ProgressDialog(getContext());
                pDialog.setMessage("Cargando Coordenadas");
                pDialog.setCancelable(false);
                pDialog.show();
                goToLocationFromAddress(origen, destino);

            }
        });


        get.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* txtLatInicio.setText("4.543986"); txtLongInicio.setText("-75.666736");
                //Unicentro
                txtLatFinal.setText("4.540026"); txtLongFinal.setText("-75.665479");*/
                final String nameruta = addnameRuta.getText().toString().trim();
                final String LatInicio = txtLatInicio.getText().toString().trim();
                final String LongInicio = txtLongInicio.getText().toString().trim();
                final String LatFinal = txtLatFinal.getText().toString().trim();
                final String LongFinal = txtLongFinal.getText().toString().trim();
                if (TextUtils.isEmpty(nameruta)) {
                    addnameRuta.setError("Favor Ingresar Nombre de Ruta");
                    addnameRuta.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(LatInicio)) {
                    txtLatInicio.setError("Favor Ingresar Latitud Inicial");
                    txtLatInicio.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(LongInicio)) {
                    txtLongInicio.setError("Favor Ingresar Longitud Inicial");
                    txtLongInicio.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(LatFinal)) {
                    txtLatFinal.setError("Favor Ingresar Latitud Final");
                    txtLatFinal.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(LongFinal)) {
                    txtLongFinal.setError("Favor Ingresar Longitud Final");
                    txtLongFinal.requestFocus();
                    return;
                }

                EnviarForm();
            }

        });

        request= Volley.newRequestQueue(getActivity().getApplicationContext());
        return view;
    }

    /**
     *Alerta para guardado de datos
     * */

    public void EnviarForm(){
        AlertDialog.Builder myBuild = new AlertDialog.Builder(getContext());
        myBuild.setTitle("Mensaje");
        myBuild.setMessage("¿Esta seguro de guardar esta ruta?");
        myBuild.setIcon(R.drawable.ic_warning_black_24dp);
        myBuild.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              GuardarRuta();
            }
        });
        myBuild.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = myBuild.create();
        dialog.show();
    }

    public void goToLocationFromAddress(String strAddress, String strAddress2) {
        //Create coder with Activity context - this
        Geocoder coder = new Geocoder(getActivity().getApplicationContext());
        List<Address> address;
        List<Address> address2;

        try {
            //Get latLng from String
            address = coder.getFromLocationName(strAddress, 5);
            address2 = coder.getFromLocationName(strAddress2, 5);

            //check for null
            if (address != null && address2 != null) {

                //Lets take first possibility from the all possibilities.
                try {
                    Address origen = address.get(0);
                    Address destino = address2.get(0);
                    LatLng OrigenRuta = new LatLng(origen.getLatitude(), origen.getLongitude());
                    LatLng DestinoRuta = new LatLng(destino.getLatitude(), destino.getLongitude());
                    //capturando resultados en variables separadas
                    double latitudInicial = OrigenRuta.latitude;
                    double longitudInicial = OrigenRuta.longitude;
                    double latitudFinal = DestinoRuta.latitude;
                    double longitudFinal = DestinoRuta.longitude;


                    //convirtiendo a string resultados double
                    String LatInicial = new Double(latitudInicial).toString();
                    String LongInicial = new Double(longitudInicial).toString();
                    String LatFinal = new Double(latitudFinal).toString();
                    String LongFinal = new Double(longitudFinal).toString();


                    Utilidades.coordenadas.setLatitudInicial(Double.valueOf(LatInicial));
                    Utilidades.coordenadas.setLongitudInicial(Double.valueOf(LongInicial));
                    Utilidades.coordenadas.setLatitudFinal(Double.valueOf(LatFinal));
                    Utilidades.coordenadas.setLongitudFinal(Double.valueOf(LongFinal));

                    webServiceObtenerRuta(LatInicial,LongInicial,
                            LatFinal,LongFinal);


                    //llenando campos de coordenadas
                /*    txtLatInicio.setText(LatInicial);
                    txtLongInicio.setText(LongInicial);
                    txtLatFinal.setText(LatFinal);
                    txtLongFinal.setText(LongFinal);*/
                    //Animate and Zoom on that map location
                  //    mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                  //  mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
                } catch (IndexOutOfBoundsException er) {
                    Toast.makeText(getActivity().getApplicationContext(), "Direcciones no Validas", Toast.LENGTH_SHORT).show();
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void webServiceObtenerRuta(String latitudInicial, String longitudInicial, String latitudFinal, String longitudFinal) {

        String url="https://maps.googleapis.com/maps/api/directions/json?origin="+latitudInicial+","+longitudInicial
                +"&destination="+latitudFinal+","+longitudFinal+"&key=AIzaSyCGSDxyyedIRTb3CEzPu1jN8mbvs7BmL2c";

        jsonObjectRequest=new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                //Este método PARSEA el JSONObject que retorna del API de Rutas de Google devolviendo
                //una lista del lista de HashMap Strings con el listado de Coordenadas de Lat y Long,
                //con la cual se podrá dibujar pollinas que describan la ruta entre 2 puntos.
                JSONArray jRoutes = null;
                JSONArray jLegs = null;
                JSONArray jSteps = null;

                try {

                    jRoutes = response.getJSONArray("routes");

                    /** Traversing all routes */
                    for(int i=0;i<jRoutes.length();i++){
                        jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
                        List<HashMap<String, String>> path = new ArrayList<HashMap<String, String>>();

                        /** Traversing all legs */
                        for(int j=0;j<jLegs.length();j++){
                            jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");

                            /** Traversing all steps */
                            for(int k=0;k<jSteps.length();k++){
                                String polyline = "";
                                polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                                List<LatLng> list = decodePoly(polyline);

                                /** Traversing all points */
                                for(int l=0;l<list.size();l++){
                                    HashMap<String, String> hm = new HashMap<String, String>();
                                    hm.put("lat", Double.toString(((LatLng)list.get(l)).latitude) );
                                    hm.put("lng", Double.toString(((LatLng)list.get(l)).longitude) );
                                    path.add(hm);
                                }
                            }
                            Utilidades.routes.add(path);
                            if(path != null){
                                if (pDialog.isShowing())
                                    pDialog.dismiss();
                                Intent miIntent=new Intent(getActivity(), MapsActivity.class);
                                startActivity(miIntent);
                                return;
                            }

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }catch (Exception e){
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getActivity(), "No se puede conectar "+error.toString(), Toast.LENGTH_LONG).show();
                System.out.println();
                Log.d("ERROR: ", error.toString());
            }
        }
        );

        request.add(jsonObjectRequest);
    }

    public List<List<HashMap<String,String>>> parse(JSONObject jObject){
        //Este método PARSEA el JSONObject que retorna del API de Rutas de Google devolviendo
        //una lista del lista de HashMap Strings con el listado de Coordenadas de Lat y Long,
        //con la cual se podrá dibujar pollinas que describan la ruta entre 2 puntos.
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;

        try {

            jRoutes = jObject.getJSONArray("routes");

            /** Traversing all routes */
            for(int i=0;i<jRoutes.length();i++){
                jLegs = ( (JSONObject)jRoutes.get(i)).getJSONArray("legs");
                List<HashMap<String, String>> path = new ArrayList<HashMap<String, String>>();

                /** Traversing all legs */
                for(int j=0;j<jLegs.length();j++){
                    jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");

                    /** Traversing all steps */
                    for(int k=0;k<jSteps.length();k++){
                        String polyline = "";
                        polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                        List<LatLng> list = decodePoly(polyline);

                        /** Traversing all points */
                        for(int l=0;l<list.size();l++){
                            HashMap<String, String> hm = new HashMap<String, String>();
                            hm.put("lat", Double.toString(((LatLng)list.get(l)).latitude) );
                            hm.put("lng", Double.toString(((LatLng)list.get(l)).longitude) );
                            path.add(hm);
                        }
                    }
                    Utilidades.routes.add(path);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
        }
        return Utilidades.routes;
    }


    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }


    private void GuardarRuta() {
        final String nameruta = addnameRuta.getText().toString().trim();
        final String LatInicio = txtLatInicio.getText().toString().trim();
        final String LongInicio = txtLongInicio.getText().toString().trim();
        final String LatFinal = txtLatFinal.getText().toString().trim();
        final String LongFinal = txtLongFinal.getText().toString().trim();

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_guardar,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            //converting response to json object
                            //JSONObject obj = new JSONObject(response);

                            JSONObject obj = new JSONObject(response);

                            //if no error in response
                            if (!obj.getBoolean("error")) {

                                Toast.makeText(getActivity(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                            } else {
                                addnameRuta.setText("");
                                txtLatInicio.setText("");
                                txtLongInicio.setText("");
                                txtLatFinal.setText("");
                                txtLongFinal.setText("");
                                Toast.makeText(getActivity(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("nameruta", nameruta);
                params.put("latinicio", LatInicio);
                params.put("longinicio", LongInicio);
                params.put("latfinal", LatFinal);
                params.put("longfinal", LongFinal);
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }




}


