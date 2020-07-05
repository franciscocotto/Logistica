package com.example.logistica.ui.driver;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.logistica.R;
import com.example.logistica.ui.home.HomeFragment;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import static com.example.logistica.ui.driver.ConsultaConductor.*;

public class consultarConductor extends Fragment {

    public consultarConductor() {
        // Required empty public constructor
    }

    ProgressBar progressBar;
    ProgressDialog pDialog;
    EditText etBuscar;
    Button btnBuscar, btnRegresar;
    //TableLayout tlLista;
    ListView lista;
    ArrayAdapter adapter = null;
    String[] nombres = new String[0];
    String[] idConductor = new String[0];
    String isbnParam = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_consulta_conductor, container, false);
        etBuscar = (EditText)view.findViewById(R.id.edtBuscar);
        btnBuscar = (Button)view.findViewById(R.id.btnBuscar);
        lista = (ListView)view.findViewById(R.id.lvLibros);
        progressBar = (ProgressBar)view.findViewById(R.id.progressBar);
        btnRegresar = (Button) view.findViewById(R.id.btnRegresarBusqueda);
        pDialog = new ProgressDialog(getContext());
        pDialog.setMessage("Cargando Datos");
        pDialog.setCancelable(false);
        pDialog.show();
        buscarConductor(" ", 1);
        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etBuscar.getText().toString().isEmpty()){
                    EnviarForm();
                }
                else{
                    pDialog = new ProgressDialog(getContext());
                    pDialog.setMessage("Buscando...");
                    pDialog.setCancelable(false);
                    pDialog.show();
                    String busqueda = etBuscar.getText().toString();
                    buscarConductor(busqueda,2);
                    if(lista.getCount()== 0){
                        Toast.makeText(getActivity().getApplicationContext(), "Busqueda Finalizada", Toast.LENGTH_LONG).show();
                    }
                }

            }
        });
        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    ConsultaConductor.setIdConductorAux(idConductor[position]);
                    ConsultaConductor.sethintEdit("mod");
                    Conductor conductor = new Conductor();
                    FragmentTransaction fr = getFragmentManager().beginTransaction();
                    fr.replace(R.id.nav_host_fragment, new Conductor());
                    fr.commit();
            }
        });
        view.findViewById(R.id.btnRegresar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegresarBusqueda();
            }
        });
        return view;
    }

    public void buscarConductor(final String busqueda, int accion){
        String URL = null;
        switch (accion){
            case 1: //Consulta de docuemntos
                    URL = "https://inventario-pdm115.000webhostapp.com/Logistica/ws_vc17009/ws_consultaConductores.php";
                break;

            case 2: //Buscar documentos
                    URL = "https://inventario-pdm115.000webhostapp.com/Logistica/ws_vc17009/ws_buscarConductor.php";
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

                            ArrayList<ConsultaConductor> listB = new ArrayList<ConsultaConductor>();
                            for (int i = 0; i < bdoc.length(); i += 11) {
                                try {
                                    listB.add(new ConsultaConductor(
                                            bdoc.getString(i+1 ),
                                            bdoc.getString(i + 2),
                                            bdoc.getString(i + 3),
                                            bdoc.getString(i + 4),
                                            bdoc.getString(i + 5),
                                            bdoc.getString(i + 6),
                                            bdoc.getString(i + 7),
                                            bdoc.getString(i + 8),
                                            bdoc.getString(i + 9),
                                            bdoc.getString(i + 10)));
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
            nombres = new String[list.size()];
            idConductor = new String[list.size()];
            ArrayList<ConsultaConductor> docu = new ArrayList<ConsultaConductor>();
            docu = list;
            for (int i=0; i<list.size();i++){
                nombres[i] = docu.get(i).getNombre().toString();
                idConductor[i] = docu.get(i).getId_conductor().toString();
            }
            adapter= new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, nombres);
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

    public void EnviarForm(){
        AlertDialog.Builder myBuild = new AlertDialog.Builder(getContext());
        myBuild.setTitle("Mensaje");
        myBuild.setMessage("No ha Seleccionado un conductor Especifico, ¿Desea mostrar todos los conductor " +
                " Disponibles?");
        myBuild.setIcon(R.drawable.ic_error_outline_black_24dp);
        myBuild.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Toast.makeText(getActivity().getApplicationContext(), "Campo de busqueda vacio", Toast.LENGTH_LONG).show();
                buscarConductor(" ",1);
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
}
