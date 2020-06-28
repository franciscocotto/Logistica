package com.example.logistica;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.logistica.ui.ruta.addRuta;
import com.example.logistica.ui.ruta.editRuta;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    //Declaramos las variables para cada elemento que vamos a obtener desde nuestro fragment_con_documentos
    EditText txtLatInicio,txtLongInicio,txtLatFinal,txtLongFinal, addnameRuta, txtorigen, txtdestino;
    private int id_idi, id_cat;
    Button btnEliminar, btnRegresar;
    //Declaramos variables para el envío de datos al webService
    ProgressBar progressBar;
    ProgressDialog pDialog;

    private GoogleMap mMap;

    Button btnEditar;

    Double latInicial,longInicial,latFinal,longFinal;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.consulta_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        Button regresar = (Button) findViewById(R.id.regresar);
        btnEditar = (Button) findViewById(R.id.btnSave);

        btnEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EnviarForm();
            }
        });

        regresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void cargarRutaConsultada(){
        final String URLB = "https://inventario-pdm115.000webhostapp.com/Logistica/ws_ca06025/ws_CargarDatosRuta.php";

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.DEPRECATED_GET_OR_POST, URLB, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                response = response.replace("][", ",");
                if (response.length() > 0) {
                    try {
                        JSONArray bdoc = new JSONArray(response);
                        Log.i("sizejson", "" + bdoc.length());

                        ArrayList<Ruta> listB = new ArrayList<Ruta>();
                        for (int i = 0; i < bdoc.length(); i += 9) {
                            try {

                                listB.add(new Ruta(
                                        bdoc.getInt(i + 0),
                                        bdoc.getInt(i + 1),
                                        bdoc.getString(i + 2),
                                        bdoc.getString(i + 3),
                                        bdoc.getString(i + 4),
                                        bdoc.getString(i + 5),
                                        bdoc.getString(i + 6),
                                        bdoc.getString(i + 7),
                                        bdoc.getString(i+ 8)));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                       EditarRuta(listB);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), "No se encontraron coincidencias", Toast.LENGTH_LONG).show();
                    if (pDialog.isShowing())
                        pDialog.dismiss();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parametros = new HashMap<String, String>();
                String busqueda = Ruta.getIdRuta();
                parametros.put("campo", busqueda);
                return parametros;
            }
        };
        requestQueue.add(stringRequest);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera

        //    mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        /////////////
        LatLng center = null;
        ArrayList<LatLng> points = null;
        PolylineOptions lineOptions = null;

        // setUpMapIfNeeded();

        // recorriendo todas las rutas
        for(int i=0;i<Utilidades.routes.size();i++){
            points = new ArrayList<LatLng>();
            lineOptions = new PolylineOptions();

            // Obteniendo el detalle de la ruta
            List<HashMap<String, String>> path = Utilidades.routes.get(i);

            // Obteniendo todos los puntos y/o coordenadas de la ruta
            for(int j=0;j<path.size();j++){
                HashMap<String,String> point = path.get(j);

                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);

                if (center == null) {
                    //Obtengo la 1ra coordenada para centrar el mapa en la misma.
                    center = new LatLng(lat, lng);
                }
                points.add(position);
            }

            // Agregamos todos los puntos en la ruta al objeto LineOptions
            lineOptions.addAll(points);
            //Definimos el grosor de las Polilíneas
            lineOptions.width(2);
            //Definimos el color de la Polilíneas
            lineOptions.color(Color.BLUE);
        }

        // Dibujamos las Polilineas en el Google Map para cada ruta
        mMap.addPolyline(lineOptions);

        LatLng origen = new LatLng(Utilidades.coordenadas.getLatitudInicial(), Utilidades.coordenadas.getLongitudInicial());
        mMap.addMarker(new MarkerOptions().position(origen).title("Lat: "+Utilidades.coordenadas.getLatitudInicial()+" - Long: "+Utilidades.coordenadas.getLongitudInicial()));

        LatLng destino = new LatLng(Utilidades.coordenadas.getLatitudFinal(), Utilidades.coordenadas.getLongitudFinal());
        mMap.addMarker(new MarkerOptions().position(destino).title("Lat: "+Utilidades.coordenadas.getLatitudFinal()+" - Long: "+Utilidades.coordenadas.getLongitudFinal()));

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(center, 15));
        /////////////

    }


    /**
     *Alerta para guardado de datos
     * */

    public void EnviarForm(){
        AlertDialog.Builder myBuild = new AlertDialog.Builder(this);
        myBuild.setTitle("Mensaje");
        myBuild.setMessage("¿Esta seguro de Editar esta ruta?");
        myBuild.setIcon(R.drawable.ic_warning_black_24dp);
        myBuild.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cargarRutaConsultada();
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

    public void EditarRuta(ArrayList<Ruta> listB){
        this.getSupportFragmentManager().beginTransaction()
                .add(R.id.map_conta, new addRuta(), "editRuta").addToBackStack(null).commit();

    //    Intent miIntent = new Intent(this, editRuta.class);
     //   miIntent.putExtra("miLista", listB);
/*
        miIntent.putExtra("nameruta", listB );
        miIntent.putExtra("origen", );
        miIntent.putExtra("destino", ruta.getDestino());
        miIntent.putExtra("latinicio", ruta.getLatitudInicial());
        miIntent.putExtra("longinicio", ruta.getLongitudInicial());
        miIntent.putExtra("latfinal", ruta.getLatitudFinal());
        miIntent.putExtra("longfinal", ruta.getLongitudFinal());*/

   /*     if (pDialog.isShowing())
            pDialog.dismiss();*/
       // startActivity(miIntent);

    }


}