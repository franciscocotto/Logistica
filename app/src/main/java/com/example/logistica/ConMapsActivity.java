package com.example.logistica;

import android.app.AlertDialog;
import android.app.MediaRouteButton;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
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
import com.android.volley.toolbox.JsonObjectRequest;
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
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    //Declaramos las variables para cada elemento que vamos a obtener desde nuestro fragment_con_documentos
    EditText  addnameRuta, txtorigen, txtdestino;
    private int id_idi, id_cat;
    Button btnEliminar, btnRegresar;
    //Declaramos variables para el envío de datos al webService
    ProgressBar progressBar;
    ProgressDialog pDialog;
    String[] id_ruta = new String[0];
    String[] nameRuta = new String[0];
    String[] origen = new String[0];
    String[] destino = new String[0];
    private GoogleMap mMap;
    JsonObjectRequest jsonObjectRequest;
    RequestQueue request;
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
        int i = Ruta.getFragmento();
        addnameRuta = (EditText) findViewById(R.id.addnameRutaEdit);
        txtorigen= (EditText) findViewById(R.id.origenEdit);
        txtdestino= (EditText) findViewById(R.id.destinoEdit);
        Button btnSave = (Button) findViewById(R.id.btnSave);
        Button regresar = (Button) findViewById(R.id.regresar);
        btnEditar = (Button) findViewById(R.id.btnEdit);


         if(i==1){
             regresar.setVisibility(View.GONE);
             btnSave.setVisibility(View.GONE);
         }
         else{
             btnEditar.setVisibility(View.GONE);
             regresar.setVisibility(View.VISIBLE);
             btnSave.setVisibility(View.VISIBLE);
         }

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

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditForm();
            }
        });

    }

    public void DialogEdit(ArrayList list){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(ConMapsActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_login, null);
        final EditText addnameRuta = (EditText) mView.findViewById(R.id.addnameRutaEdit);
        final EditText txtorigen = (EditText) mView.findViewById(R.id.origenEdit);
        final EditText txtdestino = (EditText) mView.findViewById(R.id.destinoEdit);
        final EditText idText =(EditText) mView.findViewById(R.id.id_rutas);;
        id_ruta = new String[list.size()];
        nameRuta = new String[list.size()];
        origen = new String[list.size()];
        destino = new String[list.size()];

        ArrayList<Ruta> ruta = new ArrayList<Ruta>();
        ruta = list;

        for (int i=0; i<list.size();i++){
            idText.setText(id_ruta[i] = String.valueOf(ruta.get(i).getId_ruta()));
            addnameRuta.setText(nameRuta[i] = ruta.get(i).getNameruta().toString());
            txtorigen.setText(origen[i] = ruta.get(i).getOrigen().toString());
            txtdestino.setText(destino[i] = ruta.get(i).getDestino().toString());

        }

        Button viewRuta = (Button) mView.findViewById(R.id.addMappas);
        Button delete = (Button) mView.findViewById(R.id.btnDelete);
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
        viewRuta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String id = idText.getText().toString().trim();
                final String newnameruta = addnameRuta.getText().toString().trim();
                final String neworigen = txtorigen.getText().toString().trim();
                final String newdestino = txtdestino.getText().toString().trim();
                if (TextUtils.isEmpty(newnameruta)) {
                    addnameRuta.setError("Favor Ingresar el Nombre de la Ruta");
                    addnameRuta.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(neworigen)) {
                    txtorigen.setError("Favor Ingresar Origen de la Ruta");
                    txtorigen.requestFocus();
                    return;
                }
                if (TextUtils.isEmpty(newdestino)) {
                    txtdestino.setError("Favor Ingresar Destino de la Ruta");
                    txtdestino.requestFocus();
                    return;
                }

                goToLocationFromAddress(id, newnameruta, neworigen, newdestino);

            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String id = idText.getText().toString().trim();
                DeleteForm(id);
            }
        });

    }

    public void goToLocationFromAddress(String id, String nameruta, String strAddress, String strAddress2) {
        //Create coder with Activity context - this
        Geocoder coder = new Geocoder(getApplicationContext());
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

                    webServiceObtenerRuta(id, nameruta, strAddress, strAddress2, LatInicial,LongInicial,
                            LatFinal,LongFinal);

                } catch (IndexOutOfBoundsException er) {
                    if (pDialog.isShowing())
                        pDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Direcciones no Validas", Toast.LENGTH_SHORT).show();
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void webServiceObtenerRuta(final String id, final String nameruta, final String strAddress, final String strAddress2, final String latitudInicial, final String longitudInicial, final String latitudFinal, final String longitudFinal) {
        request= Volley.newRequestQueue(getApplicationContext());
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
                                Ruta.setFragmento(2);

                                Intent miIntent=new Intent(getApplicationContext(), ConMapsActivity.class);
                                miIntent.putExtra("id_ruta", id);
                                miIntent.putExtra("nameruta", nameruta);
                                miIntent.putExtra("origen", strAddress);
                                miIntent.putExtra("destino", strAddress2);
                                miIntent.putExtra("latinicio", latitudInicial);
                                miIntent.putExtra("longinicio", longitudInicial);
                                miIntent.putExtra("latfinal", latitudFinal);
                                miIntent.putExtra("longfinal", longitudFinal);

                                startActivity(miIntent);

                                return;
                            }

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Toast.makeText(getApplicationContext(), "No se puede conectar "+error.toString(), Toast.LENGTH_LONG).show();
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

                        DialogEdit(listB);

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

    @Override
    public void onBackPressed (){
        super.onBackPressed();
    }

    /**
     *Alerta para edicion de datos
     * */

    public void EditForm(){
        AlertDialog.Builder myBuild = new AlertDialog.Builder(this);
        myBuild.setTitle("Mensaje");
        myBuild.setMessage("¿Esta seguro de actualizar esta ruta?");
        myBuild.setIcon(R.drawable.ic_warning_black_24dp);
        myBuild.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditarRuta();
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

    /**
     *Alerta para eliminar de datos
     *
     * @param id*/

    public void DeleteForm(final String id){
        AlertDialog.Builder myBuild = new AlertDialog.Builder(this);
        myBuild.setTitle("Mensaje");
        myBuild.setMessage("¿Esta seguro de eliminar esta ruta?");
        myBuild.setIcon(R.drawable.ic_warning_black_24dp);
        myBuild.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EliminarRuta(id);
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
    private void EditarRuta() {
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Cargando Coordenadas");
        pDialog.setCancelable(false);
        pDialog.show();
        Intent intent = getIntent();
        String url_editar="https://inventario-pdm115.000webhostapp.com/Logistica/ws_ca06025/EditRuta.php";
        final String id = intent.getStringExtra("id_ruta");
        final String nameruta = intent.getStringExtra("nameruta");
        final String origen = intent.getStringExtra("origen");
        final String destino = intent.getStringExtra("destino");
        final String latinicio = intent.getStringExtra("latinicio");
        final String longinicio = intent.getStringExtra("longinicio");
        final String latfinal = intent.getStringExtra("latfinal");
        final String longfinal = intent.getStringExtra("longfinal");
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url_editar,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            //converting response to json object
                            //JSONObject obj = new JSONObject(response);

                            JSONObject obj = new JSONObject(response);

                            //if no error in response
                            if (!obj.getBoolean("error")) {
                                if (pDialog.isShowing())
                                    pDialog.dismiss();
                                Intent intent = new Intent(ConMapsActivity.this, Administrador.class);
                                startActivity(intent);

                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();

                            } else {
                                if (pDialog.isShowing())
                                    pDialog.dismiss();

                                Toast.makeText(getApplicationContext(), obj.getString("message"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("id_ruta", id);
                params.put("nameruta", nameruta);
                params.put("origen", origen);
                params.put("destino", destino);
                params.put("latinicio", latinicio);
                params.put("longinicio", longinicio);
                params.put("latfinal", latfinal);
                params.put("longfinal", longfinal);
                return params;
            }
        };

        requestQueue.add(stringRequest);
    }


    //Método que consume el servicio para eliminar
    public void EliminarRuta(final String id){
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Eliminando Ruta");
        pDialog.setCancelable(false);
        pDialog.show();
        final String URL = "https://inventario-pdm115.000webhostapp.com/Logistica/ws_ca06025/DeleteRuta.php";
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.POST,URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (pDialog.isShowing())
                    pDialog.dismiss();
                Intent intent = new Intent(ConMapsActivity.this, Administrador.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), "Ruta Eliminada Satisfactoriamente", Toast.LENGTH_LONG).show();
               }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (pDialog.isShowing())
                    pDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Ha ocurrido un error, no se ha podido eliminar la ruta", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("id_ruta", id);
                return params;
            }
        };
        requestQueue.add(stringRequest);
    }

}