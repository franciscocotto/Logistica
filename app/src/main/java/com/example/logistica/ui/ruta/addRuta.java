package com.example.logistica.ui.ruta;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
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

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.logistica.MapsActivity;
import com.example.logistica.R;
import com.example.logistica.Utilidades;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cz.msebera.android.httpclient.NameValuePair;

public class addRuta extends Fragment {

    private GoogleMap mMap;
    ProgressDialog pDialog;
    EditText txtLatInicio,txtLongInicio,txtLatFinal,txtLongFinal, addnameRuta, txtorigen, txtdestino;
    List<NameValuePair> lista;
    JsonObjectRequest jsonObjectRequest;
    RequestQueue request;

    public addRuta() {

    }
    ProgressBar progressBar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_ruta, container, false);
        addnameRuta = (EditText) view.findViewById(R.id.addnameRuta);
        txtorigen= (EditText) view.findViewById(R.id.origen);
        txtdestino= (EditText) view.findViewById(R.id.destino);
        Button regresar = (Button) view.findViewById(R.id.btnRegresar);
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
                final String nameruta = addnameRuta.getText().toString().trim();
                final String origen = txtorigen.getText().toString().trim();
                final String destino = txtdestino.getText().toString().trim();
                if (TextUtils.isEmpty(nameruta)) {
                    addnameRuta.setError("Favor Ingresar el Nombre de la Rutas");
                    addnameRuta.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(origen)) {
                    txtorigen.setError("Favor Ingresar Origen de la Rutas");
                    txtorigen.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(destino)) {
                    txtdestino.setError("Favor Ingresar Destino de la Rutas");
                    txtdestino.requestFocus();
                    return;
                }
                pDialog = new ProgressDialog(getContext());
                pDialog.setMessage("Cargando Coordenadas");
                pDialog.setCancelable(false);
                pDialog.show();
                goToLocationFromAddress(origen, destino);

            }
        });

        regresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ConsultaRutas consultaRutas = new ConsultaRutas();
                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.nav_host_fragment, new ConsultaRutas());
                fr.commit();
            }
        });


        request= Volley.newRequestQueue(getActivity().getApplicationContext());
        return view;
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
                    if (pDialog.isShowing())
                        pDialog.dismiss();
                    Toast.makeText(getActivity().getApplicationContext(), "Direcciones no Validas", Toast.LENGTH_SHORT).show();
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void webServiceObtenerRuta(final String latitudInicial, final String longitudInicial, final String latitudFinal, final String longitudFinal) {

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

                                final String nameruta = addnameRuta.getText().toString().trim();
                                final String origen = txtorigen.getText().toString().trim();
                                final String destino = txtdestino.getText().toString().trim();

                               // Rutas ruta=new Rutas(nameruta, origen,destino,latitudInicial,longitudInicial,latitudFinal,longitudFinal);


                                Intent miIntent=new Intent(getActivity().getBaseContext(), MapsActivity.class);
                                miIntent.putExtra("nameruta", nameruta);
                                miIntent.putExtra("origen", origen);
                                miIntent.putExtra("destino", destino);
                                miIntent.putExtra("latinicio", latitudInicial);
                                miIntent.putExtra("longinicio", longitudInicial);
                                miIntent.putExtra("latfinal", latitudFinal);
                                miIntent.putExtra("longfinal", longitudFinal);

                                if (pDialog.isShowing())
                                    pDialog.dismiss();
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






}


