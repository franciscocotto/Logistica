package com.example.logistica.ui.viajes;

import android.app.DownloadManager;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
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

    ArrayList<Viajes> viajes = new ArrayList<Viajes>();

    String[] viajesV;

    String URLBusqueda = "https://inventario-pdm115.000webhostapp.com/Logistica/ws_bg17016/ws_busqueda_viajes.php";

    EditText etBuscar;
    ImageButton btnIngresarViaje;
    ListView lvViajes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_consulta_viajes, container, false);

        lvViajes = (ListView)view.findViewById(R.id.lvViajes);
        btnIngresarViaje = (ImageButton) view.findViewById(R.id.agregarViaje);
        etBuscar = (EditText)view.findViewById(R.id.edtBuscar);

        buscarViajes(URLBusqueda, " ");

        btnIngresarViaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Viajes.accionar = 1;
                IngresarViaje ingresarViaje = new IngresarViaje();
                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.nav_host_fragment, new IngresarViaje());
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
                Viajes.accionar = 2;

            }
        });

        return view;
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
                    Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
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
        for(int i = 0; i<viajes.size();i++){
            viajesV[i] = viajes.get(i).getNomViaje();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, viajesV);
        lvViajes.setAdapter(adapter);
        updateListViewHeight(lvViajes);
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