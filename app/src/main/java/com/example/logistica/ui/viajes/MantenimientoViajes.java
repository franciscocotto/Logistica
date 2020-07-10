package com.example.logistica.ui.viajes;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.logistica.Administrador;
import com.example.logistica.Conductores;
import com.example.logistica.DirectionsJSONParser;
import com.example.logistica.R;
import com.example.logistica.Rutas;
import com.example.logistica.Vehiculos;
import com.example.logistica.Viajes;
import com.example.logistica.WS;
import com.example.logistica.dialog.DatePickerFragment;
import com.example.logistica.dialog.TimePickerFragment;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class MantenimientoViajes extends Fragment implements OnMapReadyCallback{

    //ArrayList
    ArrayList<Rutas> rutas = new ArrayList<Rutas>();
    ArrayList<Conductores> conductores = new ArrayList<Conductores>();
    ArrayList<Vehiculos> vehiculos = new ArrayList<Vehiculos>();

    //Instancias
    Viajes viajes;
    Viajes viajeCargado;

    //Vectores
    String[] rutasV;
    String[] conductoresV;
    String[] vehiculosV;

    //Variables
    private Context context;
    private Activity activity;
    private FragmentTransaction fr;
    private String url_conductor, url_vehiculo, latIni, longIni, latFin, longFin, t_origen, t_destino;
    private GoogleMap mapa;
    WS ws;
    Polyline polyline;
    Date fechaInicio, fechaFinal;

    //Objetos
    private Toast mensaje;
    private TextView tvOrigen, tvDestino, tvTelefono, tvTipoLicencia, tvTipoV, tvPlaca;
    private AutoCompleteTextView acRutas, acConductores;
    private Spinner spVehiculo;
    private EditText etNomViaje, etFechaInicio, etHoraInicio, etFechaFinal, etHoraFinal;
    private ImageView imgConductor, imgVehiculo;
    private MapView mvRutas;
    private Button btnViaje, btnRegresar, btnEliminar;
    private ProgressDialog pDialog;

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

        pDialog = new ProgressDialog(getContext());
        pDialog.setMessage("Cargando Datos");
        pDialog.setCancelable(false);
        pDialog.show();

        fr = getFragmentManager().beginTransaction();
        context = getContext();
        activity = getActivity();

        ws = new Retrofit.Builder().baseUrl("https://maps.googleapis.com/").addConverterFactory(ScalarsConverterFactory.create()).build().create(WS.class);

        //Declaracion  de AutoCompleteTextView
        acRutas = (AutoCompleteTextView)view.findViewById(R.id.acRutas);
        acConductores = (AutoCompleteTextView)view.findViewById(R.id.acConductor);

        //Declaracion de Spinner
        spVehiculo = (Spinner)view.findViewById(R.id.spVehiculo);

        //Declaracion de TextView
        tvOrigen = (TextView)view.findViewById(R.id.tvOrigen);
        tvDestino = (TextView)view.findViewById(R.id.tvDestino);
        tvTelefono = (TextView) view.findViewById(R.id.tvTelefono);
        tvTipoLicencia = (TextView) view.findViewById(R.id.tvTipoLicencia);
        tvTipoV = (TextView) view.findViewById(R.id.tvTipoV);
        tvPlaca = (TextView) view.findViewById(R.id.tvPlaca);

        //Declaracion de EditText
        etNomViaje = (EditText)view.findViewById(R.id.etNomViaje);
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

        //Declaracion de Buttons
        btnViaje = (Button)view.findViewById(R.id.btnViaje);
        btnRegresar = (Button)view.findViewById(R.id.btnRegresar);
        btnEliminar = (Button)view.findViewById(R.id.btnEliminarViaje);
        if(Viajes.accionar==1){
            btnEliminar.setVisibility(View.GONE);
        }
        if(Viajes.accionar==3){
            btnViaje.setVisibility(View.GONE);
            btnEliminar.setVisibility(View.GONE);
        }

        //Llamada a metodos de carga

        //Obtener los registros de rutas
        cargarComplementos("https://inventario-pdm115.000webhostapp.com/Logistica/ws_bg17016/ws_cargar_campos_viajes.php",1,"rutas");

        //Evento de seleccion de Ruta
        acRutas.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for(int i = 0; i<rutas.size();i++){
                    if(acRutas.getText().toString().equals(rutasV[i])){
                        latIni = rutas.get(i).getLatitudInicial();
                        longIni = rutas.get(i).getLongitudInicial();
                        latFin = rutas.get(i).getLatitudFinal();
                        longFin = rutas.get(i).getLongitudFinal();
                        t_origen = rutas.get(i).getOrigen();
                        t_destino = rutas.get(i).getDestino();
                        obtenerRutaWs(latIni, longIni, latFin, longFin);
                        tvOrigen.setText(t_origen);
                        tvDestino.setText(t_destino);
                    }
                    else {
                        mapa.clear();
                    }
                }
            }
        });

        //Evento de seleccion de Conductor
        acConductores.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for(int i = 0; i<conductores.size();i++){
                    if(acConductores.getText().toString().equals(conductoresV[i])){
                        url_conductor = conductores.get(i).getUrl_foto();
                        tvTelefono.setText(conductores.get(i).getTelefono());
                        tvTipoLicencia.setText(conductores.get(i).getTipo_licencia());
                    }
                }
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
                    tvTipoV.setText(vehiculos.get(position-1).getTipo());
                    tvPlaca.setText(vehiculos.get(position-1).getPlaca());
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

        btnViaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Viajes.accionar==1){
                    EnviarForm(1,"Esta seguro que desea registrar este viaje?");
                }
               else if(Viajes.accionar==2){
                    EnviarForm(1,"Esta seguro que desea modificar este viaje?");
                }
            }
        });
        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EnviarForm(2,"Esta seguro que desea eliminar este viaje?");
            }
        });
        btnRegresar.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
          RegresarBusqueda();
      }
  });
        return view;
    }
    /**
     *Alerta para guardado de datos
     * */

    public void EnviarForm(final int i, String mensaje){
        AlertDialog.Builder myBuild = new AlertDialog.Builder(getActivity());
        myBuild.setTitle("Mensaje");
        myBuild.setMessage(mensaje);
        myBuild.setIcon(R.drawable.ic_warning_black_24dp);
        myBuild.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (i){
                    case 1:
                        try {
                            generarViaje();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        break;
                    case 2:
                        eliminarViaje();
                        break;
                }

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
        ConsultaViajes homeFragment = new ConsultaViajes();
        FragmentTransaction fr = getFragmentManager().beginTransaction();
        fr.replace(R.id.nav_host_fragment, new ConsultaViajes());
        fr.commit();

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
        LatLng center = new LatLng(13.748168,-88.932602);
        mapa.animateCamera(CameraUpdateFactory.newLatLngZoom(center, 7));
        //mapa.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
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
                                            object.getString("dui"),
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
                                            object.getString("placa"),
                                            object.getString("url_img")));
                                }
                                cargarVehiculos(vehiculos);
                            }
                            catch (JSONException e){
                                Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                            }
                            break;

                        case 4:
                            try {
                                Viajes viajes = new Viajes();
                                JSONObject object = consulta.getJSONObject(0);
                                viajes.setId_viaje(Viajes.getIdViaje());
                                viajes.setId_ruta(object.getInt("id_ruta"));
                                viajes.setId_vehiculo(object.getInt("id_vehiculo"));
                                viajes.setId_conductor(object.getInt("id_conductor"));
                                viajes.setInicio(object.getString("inicio"));
                                viajes.setFinalizacion(object.getString("final"));
                                viajes.setNomViaje(object.getString("nom_viaje"));
                                cargarViaje(viajes);
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
                parametros.put("id_viaje", String.valueOf(Viajes.idViaje));
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
            rutasV[i] = rutas.get(i).getDatos();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, rutasV);
        acRutas.setAdapter(adapter);
        cargarComplementos("https://inventario-pdm115.000webhostapp.com/Logistica/ws_bg17016/ws_cargar_campos_viajes.php",2,"conductores");
    }

    //Metodo que carga los conductores almacenados en un AutoComplete
    private void cargarConductores(ArrayList list){
        this.conductores = list;

        conductoresV = new String[conductores.size()];

        for (int i = 0; i<conductores.size(); i++){
            conductoresV[i] = conductores.get(i).getDatos();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, conductoresV);
        acConductores.setAdapter(adapter);
        cargarComplementos("https://inventario-pdm115.000webhostapp.com/Logistica/ws_bg17016/ws_cargar_campos_viajes.php",3,"vehiculos");
    }

    //Metodo que carga los vehiculos almacenados en un Spinner
    private void cargarVehiculos(ArrayList list){
        this.vehiculos = list;

        vehiculosV = new String[vehiculos.size()+1];
        vehiculosV[0] = " ";
        for (int i = 0; i<vehiculos.size(); i++){
            vehiculosV[i+1] = vehiculos.get(i).getDatos();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, vehiculosV);
        spVehiculo.setAdapter(adapter);
        if(Viajes.accionar==2 || Viajes.accionar==3){
            cargarComplementos("https://inventario-pdm115.000webhostapp.com/Logistica/ws_bg17016/ws_cargar_campos_viajes.php",4,"viajes");
        }
      else {
            if (pDialog.isShowing()){
                pDialog.dismiss();
            }
        }
    }

    private void cargarViaje(Object object){
        viajeCargado = (Viajes) object;

        btnViaje.setText("Modificar viaje");

        etNomViaje.setText(viajeCargado.getNomViaje());

        String[] fechaIni = viajeCargado.getInicio().split(" ");
        etFechaInicio.setText(fechaIni[0]);
        etHoraInicio.setText(fechaIni[1]);

        String[] fechaFin = viajeCargado.getFinalizacion().split(" ");
        etFechaFinal.setText(fechaFin[0]);
        etHoraFinal.setText(fechaFin[1]);

        for(int i = 0; i<rutas.size();i++){
            if(viajeCargado.getId_ruta()==rutas.get(i).getId_ruta()){
                acRutas.setText(rutas.get(i).getDatos());
                latIni = rutas.get(i).getLatitudInicial();
                longIni = rutas.get(i).getLongitudInicial();
                latFin = rutas.get(i).getLatitudFinal();
                longFin = rutas.get(i).getLongitudFinal();
                t_origen = rutas.get(i).getOrigen();
                t_destino = rutas.get(i).getDestino();
                obtenerRutaWs(latIni, longIni, latFin, longFin);
                tvOrigen.setText(t_origen);
                tvDestino.setText(t_destino);
            }
            else {
                mapa.clear();
            }
        }

        for(int i = 0; i<conductores.size();i++){
            if(viajeCargado.getId_conductor()==conductores.get(i).getId_conductor()){
                acConductores.setText(conductores.get(i).getDatos());
                url_conductor = conductores.get(i).getUrl_foto();
                tvTelefono.setText(conductores.get(i).getTelefono());
                tvTipoLicencia.setText(conductores.get(i).getTipo_licencia());
            }
        }
        Picasso.get().load(url_conductor).into(imgConductor);

        for (int i = 0; i<vehiculos.size();i++){
            if(viajeCargado.getId_vehiculo()==vehiculos.get(i).getId_vehiculo()){
                spVehiculo.setSelection(i+1);
            }
        }
        if (pDialog.isShowing()){
            pDialog.dismiss();
        }
        if(Viajes.accionar == 3){
            etNomViaje.setEnabled(false);
            acConductores.setEnabled(false);
            acRutas.setEnabled(false);
            etFechaInicio.setEnabled(false);
            etHoraInicio.setEnabled(false);
            etFechaFinal.setEnabled(false);
            etHoraFinal.setEnabled(false);
            spVehiculo.setEnabled(false);

        }
    }

    //Metodo que ingresa o modifica viajes
    private void generarViaje() throws ParseException {
        if(etNomViaje.getText().toString().isEmpty() || acRutas.getText().toString().isEmpty() || etFechaInicio.getText().toString().isEmpty() ||
        etHoraInicio.getText().toString().isEmpty() || etFechaFinal.getText().toString().isEmpty() ||
                etHoraFinal.getText().toString().isEmpty() || acConductores.getText().toString().isEmpty() || (spVehiculo.getSelectedItemId()==0)){
            if(etNomViaje.getText().toString().isEmpty()){
                etNomViaje.setError("Campo obligatorio");
            }
            if(acRutas.getText().toString().isEmpty()){
                acRutas.setError("Campo obligatorio");
            }
            if(etFechaInicio.getText().toString().isEmpty()){
                etFechaInicio.setError("Campo obligatorio");
            }
            if(etHoraInicio.getText().toString().isEmpty()){
                etHoraInicio.setError("Campo obligatorio");
            }
            if(etFechaFinal.getText().toString().isEmpty()){
                etFechaFinal.setError("Campo obligatorio");
            }
            if(etHoraFinal.getText().toString().isEmpty()){
                etHoraFinal.setError("Campo obligatorio");
            }
            if(acConductores.getText().toString().isEmpty()){
                acConductores.setError("Campo obligatorio");
            }
            if(spVehiculo.getSelectedItemId()==0){
                Toast.makeText(getContext(), "Seleccione un vehiculo", Toast.LENGTH_SHORT).show();
            }
            Toast.makeText(getContext(), "Faltan datos obligatorios", Toast.LENGTH_LONG).show();
        }
        else {
            if(!acRutas.getText().toString().isEmpty() && !acConductores.getText().toString().isEmpty()){
                boolean ruta = false;
                boolean conductor = false;
                boolean fechas = true;
                if(!acRutas.getText().toString().isEmpty()){
                    for (int i = 0; i<rutasV.length;i++){
                        if (acRutas.getText().toString().equals(rutasV[i])){
                            ruta = true;
                        }
                    }
                    if (ruta==false){
                        acRutas.setError("Dato invalido");
                    }
                }
                if(!acConductores.getText().toString().isEmpty()){
                    for (int i = 0; i<conductoresV.length;i++){
                        if (acConductores.getText().toString().equals(conductoresV[i])){
                            conductor = true;
                        }
                    }
                    if (conductor==false){
                        acConductores.setError("Dato invalido");
                    }
                }
                SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                try {
                    fechaInicio = formato.parse(etFechaInicio.getText().toString()+" "+etHoraInicio.getText().toString());
                    fechaFinal = formato.parse(etFechaFinal.getText().toString()+" "+etHoraFinal.getText().toString());
                }
                catch (ParseException ex){
                    Toast.makeText(getContext(), ex.toString(), Toast.LENGTH_SHORT).show();
                }

                if(fechaFinal.before(fechaInicio)){
                    etFechaInicio.setError("Error con fechas ingresadas");
                    etFechaFinal.setError("Error con fechas ingresadas");
                    etHoraInicio.setError("Error con horas ingresadas");
                    etHoraFinal.setError("Error con horas ingresadas");
                    Toast.makeText(getContext(), "Incoherencia en programacion de inicio y final del viaje.",Toast.LENGTH_LONG).show();
                    fechas = false;
                }
                if(ruta==false || conductor==false || fechas==false){
                    Toast.makeText(getContext(), "Algunos datos son invalidos",Toast.LENGTH_LONG).show();
                }
                else {
                    viajes = new Viajes();
                    viajes.setNomViaje(etNomViaje.getText().toString());
                    viajes.setInicio(etFechaInicio.getText().toString()+" "+etHoraInicio.getText().toString());
                    viajes.setFinalizacion(etFechaFinal.getText().toString()+" "+etHoraFinal.getText().toString());

                    for (int i = 0; i<rutasV.length;i++){
                        if(acRutas.getText().toString().equals(rutas.get(i).getDatos())){
                            viajes.setId_ruta(rutas.get(i).getId_ruta());
                        }
                    }
                    for (int i = 0;i<conductoresV.length;i++){
                        if(acConductores.getText().toString().equals(conductores.get(i).getDatos())){
                            viajes.setId_conductor(conductores.get(i).getId_conductor());
                        }
                    }
                    viajes.setId_vehiculo(vehiculos.get(spVehiculo.getSelectedItemPosition()-1).getId_vehiculo());
                    if(Viajes.accionar==1){
                        viajes.cargarViajes(context,"https://inventario-pdm115.000webhostapp.com/Logistica/ws_bg17016/ws_viajes.php","insertar", fr, activity);
                    }
                    else if(Viajes.accionar==2){
                        if(viajes.getId_ruta()==viajeCargado.getId_ruta() && viajes.getId_conductor()==viajeCargado.getId_conductor() &&
                        viajes.getId_vehiculo()==viajeCargado.getId_vehiculo() && viajes.getInicio().equals(viajeCargado.getInicio()) &&
                        viajes.getFinalizacion().equals(viajeCargado.getFinalizacion()) && viajes.getNomViaje().equals(viajeCargado.getNomViaje())){
                            Toast.makeText(getContext(), "Datos sin modificacion", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            viajes.setId_viaje(viajeCargado.getId_viaje());
                            viajes.cargarViajes(context,"https://inventario-pdm115.000webhostapp.com/Logistica/ws_bg17016/ws_viajes.php","modificar", fr, activity);
                        }

                    }
                }
            }
        }
    }

    private void eliminarViaje(){
        viajes = new Viajes();
        viajes = viajeCargado;
        viajes.cargarViajes(context,"https://inventario-pdm115.000webhostapp.com/Logistica/ws_bg17016/ws_viajes.php","eliminar", fr, activity);
    }

    public static void regresarConsulta(FragmentTransaction fr, Activity activity){
        ConsultaViajes consultaViajes = new ConsultaViajes();
        fr.replace(R.id.nav_host_fragment, new ConsultaViajes());
        fr.commit();
        ((Administrador)activity).getSupportActionBar().setTitle("Consultar viajes");
    }

    //Metodo que manda las coordenadas al servicio de Google maps
    private void obtenerRutaWs(String lat_origen, String long_origen, String lat_destino, String long_destino){
        String url;
        try {
            url = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "origin="+lat_origen+","+long_origen+
                    "&destination="+lat_destino+","+long_destino +
                    "&key=AIzaSyCGSDxyyedIRTb3CEzPu1jN8mbvs7BmL2c";

            Log.e("URL","url");
            ws.obtenerRuta(url).enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, retrofit2.Response<String> response) {
                    new ParserTask().execute(response.body().toString());
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {

                }
            });
        }
        catch (Exception e){

        }
    }

    //Metodo que convierte los datos en puntos geograficos
    public class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>>>{

        ProgressDialog progressDialog = new ProgressDialog(getContext());
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Cargando...");
            progressDialog.show();
        }

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject;
            List<List<HashMap<String,String>>> routes = null;
            try {
                jsonObject = new JSONObject(strings[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();
                routes = parser.parse(jsonObject);
            }
            catch (Exception e){
                Log.e("error",e.toString());
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            super.onPostExecute(lists);
            progressDialog.dismiss();
            mapa.clear();
            ArrayList points = null;
            PolylineOptions polylineOptions = null;
            for (int i = 0; i<lists.size();i++){
                points = new ArrayList<LatLng>();
                polylineOptions = new PolylineOptions();
                List<HashMap<String, String>>path = lists.get(i);
                for (int j = 2; j<path.size();j++){
                    HashMap<String, String> point = path.get(j);
                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat,lng);
                    points.add(position);
                }
                polylineOptions.addAll(points);
                polylineOptions.width(10);
                polylineOptions.color(getContext().getResources().getColor(R.color.colorPrimary));
                polylineOptions.geodesic(true);
            }
            polyline = mapa.addPolyline(polylineOptions);

            LatLng origen = new LatLng(Double.valueOf(latIni), Double.valueOf(longIni));
            mapa.addMarker(new MarkerOptions().position(origen).title("Origen: "+t_origen));

            LatLng destino = new LatLng(Double.valueOf(latFin), Double.valueOf(longFin));
            mapa.addMarker(new MarkerOptions().position(destino).title("Destino: "+t_destino));

            LatLngBounds.Builder constructor = new LatLngBounds.Builder();
            constructor.include(origen);
            constructor.include(destino);

            LatLngBounds limites = constructor.build();
            int ancho = getResources().getDisplayMetrics().widthPixels;
            int alto = getResources().getDisplayMetrics().heightPixels;
            int padding = (int)(alto*0.25);
            CameraUpdate centro = CameraUpdateFactory.newLatLngBounds(limites, ancho, alto, padding);
            mapa.animateCamera(centro);
        }
    }
}
