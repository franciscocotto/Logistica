package com.example.logistica.ui.ruta;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.logistica.ConMapsActivity;
import com.example.logistica.R;
import com.example.logistica.Rutas;
import com.example.logistica.Utilidades;
import com.example.logistica.ui.home.HomeFragment;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ConsultaRutas extends Fragment {

    public ConsultaRutas() {
        // Required empty public constructor
    }
    private static final int REQUEST_CODE = 1 ;
    public static final int RESULT_OK = -1;
    JsonObjectRequest jsonObjectRequest;
    RequestQueue request;
    ProgressBar progressBar;
    ProgressDialog pDialog;
    EditText etBuscar;
    Button btnBuscar, btnRegresar;
    //TableLayout tlLista;
    ListView lista;
    ArrayAdapter adapter = null;
    String[] nameRuta = new String[0];
    String[] latitudInicial = new String[0];
    String[] longitudInicial = new String[0];
    String[] latitudFinal = new String[0];
    String[] longitudFinal = new String[0];
    String[] idruta = new String[0];
    String isbnParam = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_consulta_rutas, container, false);
        ImageButton add = (ImageButton) view.findViewById(R.id.agregarRuta);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRuta addruta = new addRuta();
                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.nav_host_fragment, new addRuta());
                fr.commit();
                /* startActivity(new Intent(getContext(), MapsActivity.class));*/
            }
        });

        etBuscar = (EditText)view.findViewById(R.id.edtBuscar);
        btnBuscar = (Button)view.findViewById(R.id.btnBuscar);
        lista = (ListView)view.findViewById(R.id.lvRutas);
        progressBar = (ProgressBar)view.findViewById(R.id.progressBar);
        btnRegresar = (Button) view.findViewById(R.id.btnRegresarBusqueda);
        pDialog = new ProgressDialog(getContext());
        pDialog.setMessage("Cargando Datos");
        pDialog.setCancelable(false);
        pDialog.show();
        buscarRuta(" ", 1);
        etBuscar.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(etBuscar.getText().toString().isEmpty()){
                    buscarRuta(" ",1);
                }else{
                    String busqueda = etBuscar.getText().toString();
                    buscarRuta(busqueda,2);
                }
            }
        });


        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                pDialog = new ProgressDialog(getContext());
                pDialog.setMessage("Cargando Coordenadas");
                pDialog.setCancelable(false);
                pDialog.show();
                Rutas.setIdRuta(idruta[position]);
                String LatInicial = latitudInicial[position];
                String LongInicial = longitudInicial[position];
                String LatFinal = latitudFinal[position];
                String LongFinal = longitudFinal[position];


                Utilidades.coordenadas.setLatitudInicial(Double.valueOf(LatInicial));
                Utilidades.coordenadas.setLongitudInicial(Double.valueOf(LongInicial));
                Utilidades.coordenadas.setLatitudFinal(Double.valueOf(LatFinal));
                Utilidades.coordenadas.setLongitudFinal(Double.valueOf(LongFinal));

                webServiceObtenerRuta(LatInicial,LongInicial,
                        LatFinal,LongFinal);


            }
        });

        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegresarBusqueda();
            }
        });

      //  Button voz = (Button) view.findViewById(R.id.Audio);

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String DIALOG_TEXT = "Speech recognition demo";
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, DIALOG_TEXT);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, REQUEST_CODE);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "es-SV");

                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        return view;
    }
    String resultSpeech = "";
    @Override
    public void onActivityResult(int requestCode, int resultcode, Intent intent) {
        super.onActivityResult(requestCode, resultcode, intent);
        ArrayList<String> speech;
        if (resultcode == RESULT_OK) {
            if (requestCode == REQUEST_CODE) {
                speech = intent.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                resultSpeech = speech.get(0);
                etBuscar.setText(resultSpeech);
                pDialog.setMessage("Buscando...");
                pDialog.setCancelable(false);
                pDialog.show();
                String busqueda = etBuscar.getText().toString();
                buscarRuta(busqueda,2);
                if(lista.getCount()== 0){
                    Toast.makeText(getActivity(), "Busqueda Finalizada", Toast.LENGTH_LONG).show();
                }

            }
        }
    }

    public void buscarRuta(final String busqueda, int accion){
        String URL = null;
        switch (accion){
            case 1: //Consulta de Rutas
                URL = "https://inventario-pdm115.000webhostapp.com/Logistica/ws_ca06025/ws_consulta_ruta.php";

                break;

            case 2: //Buscar Rutas
                    URL = "https://inventario-pdm115.000webhostapp.com/Logistica/ws_ca06025/ws_buscar_ruta.php";
                break;
        }

            lista.setAdapter(null);

            RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
            StringRequest stringRequest = new StringRequest(Request.Method.DEPRECATED_GET_OR_POST, URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    response = response.replace("][", ",");
                    if (response.length() > 0) {
                        try {
                            JSONArray bdoc = new JSONArray(response);
                            Log.i("sizejson", "" + bdoc.length());

                            ArrayList<Rutas> listB = new ArrayList<Rutas>();
                            for (int i = 0; i < bdoc.length(); i += 9) {
                                try {

                                    listB.add(new Rutas(
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
                            cargarTabla(listB);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    else{
                        Toast.makeText(getActivity().getApplicationContext(), "No se encontraron coincidencias", Toast.LENGTH_LONG).show();
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
                    parametros.put("campo", busqueda);
                    return parametros;
                }
            };
            requestQueue.add(stringRequest);

    }


    public void cargarTabla(ArrayList list){

            nameRuta = new String[list.size()];
            latitudInicial = new String[list.size()];
            longitudInicial = new String[list.size()];
            latitudFinal = new String[list.size()];
            longitudFinal = new String[list.size()];
            idruta = new String[list.size()];
            ArrayList<Rutas> ruta = new ArrayList<Rutas>();
            ruta = list;
            for (int i=0; i<list.size();i++){
                nameRuta[i] = ruta.get(i).getNameruta().toString();
                 idruta[i] = String.valueOf(ruta.get(i).getId_ruta());
                 latitudInicial[i] = ruta.get(i).getLatitudInicial().toString();
                 longitudInicial[i] = ruta.get(i).getLongitudInicial().toString();
                 latitudFinal[i] = ruta.get(i).getLatitudFinal().toString();
                 longitudFinal[i] = ruta.get(i).getLongitudFinal().toString();
            }

            adapter= new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, nameRuta);
            lista.setAdapter(adapter);
            updateListViewHeight(lista);
            if (pDialog.isShowing())
                pDialog.dismiss();



    }

    public static void updateListViewHeight(ListView lista) {
        ListAdapter myListAdapter = lista.getAdapter();
        if (myListAdapter == null) {
            return;
        }
        // get listview height
        int totalHeight = 0;
        int adapterCount = myListAdapter.getCount();
        for (int size = 0; size < adapterCount; size++) {
            View listItem = myListAdapter.getView(size, null, lista);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        // Change Height of ListView
        ViewGroup.LayoutParams params = lista.getLayoutParams();
        params.height = (totalHeight
                + (lista.getDividerHeight() * (adapterCount)));
        lista.setLayoutParams(params);
    }

    /**
     * Alerta
     * */
    public void EnviarForm(){
        AlertDialog.Builder myBuild = new AlertDialog.Builder(getContext());
        myBuild.setTitle("Mensaje");
        myBuild.setMessage("No ha Seleccionado una Rutas en Especifico, ¿Desea mostrar todos las Rutas Disponibles?");
        myBuild.setIcon(R.drawable.ic_error_outline_black_24dp);
        myBuild.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Toast.makeText(getActivity().getApplicationContext(), "Campo de busqueda vacio", Toast.LENGTH_LONG).show();
                buscarRuta(" ",1);
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
    public  void RegresarBusqueda(){
        HomeFragment homeFragment = new HomeFragment();
        FragmentTransaction fr = getFragmentManager().beginTransaction();
        fr.replace(R.id.nav_host_fragment, new HomeFragment());
        fr.commit();

    }


    private void webServiceObtenerRuta(final String latitudInicial, final String longitudInicial, final String latitudFinal, final String longitudFinal) {
        request= Volley.newRequestQueue(getActivity().getApplicationContext());
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
                                Rutas.setFragmento(1);
                               Intent miIntent=new Intent(getActivity(), ConMapsActivity.class);
                               startActivity(miIntent);
                                if (pDialog.isShowing())
                                    pDialog.dismiss();
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
