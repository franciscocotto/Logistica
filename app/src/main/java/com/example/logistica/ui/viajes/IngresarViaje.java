package com.example.logistica.ui.viajes;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.logistica.Conductores;
import com.example.logistica.R;
import com.example.logistica.Rutas;
import com.example.logistica.Vehiculos;
import com.example.logistica.dialog.DatePickerFragment;
import com.example.logistica.dialog.TimePickerFragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class IngresarViaje extends Fragment implements OnMapReadyCallback{

    //ArrayList
    ArrayList<Rutas> rutas = new ArrayList<Rutas>();
    ArrayList<Conductores> conductores = new ArrayList<Conductores>();
    ArrayList<Vehiculos> vehiculos = new ArrayList<Vehiculos>();

    //Vectores
    String[] rutasV;
    String[] conductoresV;
    String[] vehiculosV;

    //Variables
    private String url_conductor, url_vehiculo;
    private GoogleMap mapa;

    private AutoCompleteTextView acRutas, acConductores;
    private Spinner spVehiculo;
    private EditText etFechaInicio, etHoraInicio, etFechaFinal, etHoraFinal;
    private ImageView imgConductor, imgVehiculo;
    private MapView mvRutas;

    @Override
    public void onResume(){
        super.onResume();
        mvRutas.onResume();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mvRutas.onDestroy();
    }

    @Override
    public void onPause(){
        super.onPause();
        mvRutas.onPause();
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_ingresar_viaje, container, false);

        //Declaracion  de AutoCompleteTextView
        acRutas = (AutoCompleteTextView)view.findViewById(R.id.acRutas);
        acConductores = (AutoCompleteTextView)view.findViewById(R.id.acConductor);

        //Declaracion de Spinner
        spVehiculo = (Spinner)view.findViewById(R.id.spVehiculo);

        //Declaracion de EditText
        etFechaInicio = (EditText)view.findViewById(R.id.etFechaInicio);
        etFechaFinal = (EditText)view.findViewById(R.id.etFechaFinal);
        etHoraInicio = (EditText)view.findViewById(R.id.etHoraInicio);
        etHoraFinal = (EditText)view.findViewById(R.id.etHoraFinal);

        //Declaracion de ImageView
        imgConductor = (ImageView)view.findViewById(R.id.imgConductor);
        imgVehiculo = (ImageView)view.findViewById(R.id.imgVehiculo);

        //Declaracion de MapView
        mvRutas = (MapView)view.findViewById(R.id.mvRuta);
        mvRutas.onCreate(savedInstanceState);
        mvRutas.getMapAsync(this);


        //Llamada a metodos de carga

        //Obtener los registros de rutas
        cargarComplementos("https://inventario-pdm115.000webhostapp.com/Logistica/ws_bg17016/ws_cargar_campos_viajes.php",1,"rutas");
        cargarComplementos("https://inventario-pdm115.000webhostapp.com/Logistica/ws_bg17016/ws_cargar_campos_viajes.php",2,"conductores");
        cargarComplementos("https://inventario-pdm115.000webhostapp.com/Logistica/ws_bg17016/ws_cargar_campos_viajes.php",3,"vehiculos");

        //Evento de seleccion de Conductor
        acConductores.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                url_conductor = conductores.get(position).getUrl_foto();
                Toast.makeText(getContext(), url_conductor, Toast.LENGTH_LONG).show();
                Picasso.get().load(url_conductor).into(imgConductor);
            }
        });

        //Evento de seleccion de Spinner
        spVehiculo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position>0){
                    url_vehiculo = vehiculos.get(position-1).getUrl_img();
                    Picasso.get().load(url_vehiculo).into(imgVehiculo);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //Eventos clic de calendarios
        etFechaInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(1);
            }
        });
        etFechaFinal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(2);
            }
        });

        //Eventos clic en horas
        etHoraInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(1);
            }
        });
        etHoraFinal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(2);
            }
        });

        return view;
    }

    //Metodo para mostrar DatePickerDialog
    private void showDatePickerDialog(final int id) {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // +1 because January is zero
                final String selectedDate = year + "-" + twoDigits(month+1) + "-" +twoDigits(day)  ;
                switch (id){
                    case 1:
                        etFechaInicio.setText(selectedDate);
                        break;
                    case 2:
                        etFechaFinal.setText(selectedDate);
                        break;
                }

            }

            private String twoDigits(int n) {
                return (n<=9) ? ("0"+n) : String.valueOf(n);
            }
        });

        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }

    //Metodo para mostrar TimePickerDialog
    private void showTimePickerDialog(final int id) {
        TimePickerFragment newFragment = TimePickerFragment.newInstance(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hora, int minuto) {
                // +1 because January is zero
                final String selectedHour = twoDigits(hora) + ":" + twoDigits(minuto);
                switch (id){
                    case 1:
                        etHoraInicio.setText(selectedHour);
                        break;
                    case 2:
                        etHoraFinal.setText(selectedHour);
                        break;
                }

            }

            private String twoDigits(int n) {
                return (n<=9) ? ("0"+n) : String.valueOf(n);
            }
        });

        newFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mapa = googleMap;
        mapa.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        mapa.addPolyline(new PolylineOptions().add(new LatLng(13.6976341,-89.1911956), new LatLng(13.343611099999999,-89.00638889999999)).width(5).color(Color.RED));
        UiSettings uiSettings = mapa.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
    }

    private void cargarComplementos(String URL, final int accion, final String tabla){
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.DEPRECATED_GET_OR_POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray consulta = new JSONArray(response);

                    switch (accion){

                        case 1:     //Cargar rutas almacenadas
                            ArrayList<Rutas> rutas = new ArrayList<Rutas>();
                            try {
                                for (int i = 0; i<consulta.length();i++){
                                    JSONObject object = consulta.getJSONObject(i);
                                    rutas.add(new Rutas(object.getInt("id_ruta"),
                                            object.getInt("id_estado"),
                                            object.getString("nameruta"),
                                            object.getString("origen"),
                                            object.getString("destino"),
                                            object.getString("LatInicio"),
                                            object.getString("LongInicio"),
                                            object.getString("LatFinal"),
                                            object.getString("LongFinal")));
                                }
                                cargarRutas(rutas);
                            }
                            catch (JSONException e){
                                Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                            }
                            break;

                        case 2:     //Cargar conductores almacenados
                            ArrayList<Conductores> conductores = new ArrayList<Conductores>();
                            try {
                                for (int i = 0; i<consulta.length();i++){
                                    JSONObject object = consulta.getJSONObject(i);
                                    conductores.add(new Conductores(object.getInt("id_conductor"),
                                            object.getString("nombre"),
                                            object.getString("apellido"),
                                            object.getString("telefono"),
                                            object.getString("url_foto"),
                                            object.getString("tipo_licencia")));
                                }
                                cargarConductores(conductores);
                            }
                            catch (JSONException e){
                                Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                            }
                            break;

                        case 3:     //Cargar vehiculos almacenados

                            ArrayList<Vehiculos> vehiculos = new ArrayList<Vehiculos>();
                            try {
                                for (int i = 0; i<consulta.length();i++){
                                    JSONObject object = consulta.getJSONObject(i);
                                    vehiculos.add(new Vehiculos(object.getInt("id_vehiculo"),
                                            object.getString("cod_vehiculo"),
                                            object.getString("marca"),
                                            object.getString("modelo"),
                                            object.getString("tipo"),
                                            object.getString("url_img")));
                                }
                                cargarVehiculos(vehiculos);
                            }
                            catch (JSONException e){
                                Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                            }
                            break;
                    }

                }
                catch (JSONException e){
                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
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
                parametros.put("tabla", tabla);
                return parametros;
            }
        };
        requestQueue.add(stringRequest);
    }

    //Metodo que carga las rutas almacenadas en un AutoComplete
    private void cargarRutas(ArrayList list){

        this.rutas=list;
        rutasV = new String[rutas.size()];

        for (int i = 0; i<rutas.size(); i++){
            rutasV[i] = rutas.get(i).getNameruta();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, rutasV);
        acRutas.setAdapter(adapter);
    }

    //Metodo que carga los conductores almacenados en un AutoComplete
    private void cargarConductores(ArrayList list){
        this.conductores = list;

        conductoresV = new String[conductores.size()];

        for (int i = 0; i<conductores.size(); i++){
            conductoresV[i] = conductores.get(i).getNombre()+" "+conductores.get(i).getApellido();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, conductoresV);
        acConductores.setAdapter(adapter);
    }

    //Metodo que carga los vehiculos almacenados en un Spinner
    private void cargarVehiculos(ArrayList list){
        this.vehiculos = list;

        vehiculosV = new String[vehiculos.size()+1];
        vehiculosV[0] = " ";
        for (int i = 0; i<vehiculos.size(); i++){
            vehiculosV[i+1] = vehiculos.get(i).getMarca()+" "+vehiculos.get(i).getModelo();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, vehiculosV);
        spVehiculo.setAdapter(adapter);
    }

}
