package com.example.logistica.ui.viajes;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.logistica.Administrador;
import com.example.logistica.R;
import com.example.logistica.Viajes;
import com.example.logistica.ui.home.HomeFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ConsultaViajes extends Fragment {


    public ConsultaViajes() {
        // Required empty public constructor
    }
    private static final int REQUEST_CODE = 1 ;
    public static final int RESULT_OK = -1;
    ArrayList<Viajes> viajes = new ArrayList<Viajes>();

    String[] viajesV;
    Integer[] idViaje = new Integer[0];
    String URLBusqueda = "https://inventario-pdm115.000webhostapp.com/Logistica/ws_bg17016/ws_busqueda_viajes.php";
    ProgressBar progressBar;
    ProgressDialog pDialog;
    EditText etBuscar;
    ImageButton btnIngresarViaje;
    ListView lvViajes;
    Button btnRegresar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_consulta_viajes, container, false);

        lvViajes = (ListView)view.findViewById(R.id.lvViajes);
        btnIngresarViaje = (ImageButton) view.findViewById(R.id.agregarViaje);
        etBuscar = (EditText)view.findViewById(R.id.edtBuscar);
        btnRegresar = (Button) view.findViewById(R.id.btnRegresarBusqueda);
        pDialog = new ProgressDialog(getContext());
        pDialog.setMessage("Cargando Datos");
        pDialog.setCancelable(false);
        pDialog.show();
        buscarViajes(URLBusqueda, " ");

        btnIngresarViaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Viajes.accionar = 1;
                MantenimientoViajes mantenimientoViajes = new MantenimientoViajes();
                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.nav_host_fragment, new MantenimientoViajes());
                fr.commit();
                ((Administrador) getActivity()).getSupportActionBar().setTitle("Ingresar viaje");
            }
        });

        etBuscar.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {

            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(etBuscar.getText().toString().isEmpty()){
                    buscarViajes(URLBusqueda, " ");
                }else{
                    String busqueda = etBuscar.getText().toString();
                    buscarViajes(URLBusqueda, busqueda);
                }
            }
        });

        lvViajes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Viajes.setIdViaje(idViaje[position]);
                Viajes.accionar = 2;

            }
        });
        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegresarBusqueda();
            }
        });
      Button  btnBuscar = (Button)view.findViewById(R.id.btnBuscar);
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
                buscarViajes(URLBusqueda, busqueda);
                if(lvViajes.getCount()== 0){
                    Toast.makeText(getActivity(), "Busqueda Finalizada", Toast.LENGTH_LONG).show();
                }

            }
        }
    }

    public  void RegresarBusqueda(){
        HomeFragment homeFragment = new HomeFragment();
        FragmentTransaction fr = getFragmentManager().beginTransaction();
        fr.replace(R.id.nav_host_fragment, new HomeFragment());
        fr.commit();

    }

    private void buscarViajes(String URL, final String campo){
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.DEPRECATED_GET_OR_POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray consulta = new JSONArray(response);
                    ArrayList<Viajes> viajes = new ArrayList<Viajes>();
                    for (int i = 0; i<consulta.length();i++){
                        JSONObject registro = consulta.getJSONObject(i);
                        viajes.add(new Viajes(registro.getInt("id_viaje"),
                                registro.getString("nom_viaje")));
                    }
                    cargarLista(viajes);
                }catch (JSONException e){
                    e.printStackTrace();
                  //  Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
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
                parametros.put("campo", campo);
                return parametros;
            }
        };
        requestQueue.add(stringRequest);
    }

    public void cargarLista(ArrayList list){
        this.viajes = list;
        viajesV = new String[viajes.size()];
        idViaje = new Integer[viajes.size()];
        for(int i = 0; i<viajes.size();i++){
            viajesV[i] = viajes.get(i).getNomViaje();
            idViaje[i] = viajes.get(i).getId_viaje();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, viajesV);
        lvViajes.setAdapter(adapter);
        updateListViewHeight(lvViajes);
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
}