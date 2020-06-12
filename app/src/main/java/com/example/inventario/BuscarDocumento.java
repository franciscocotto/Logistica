package com.example.inventario;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class BuscarDocumento extends Fragment {

    public BuscarDocumento() {
        // Required empty public constructor
    }

    EditText etBuscar;
    Button btnBuscar;
    TableLayout tlLista;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_buscar_documento, container, false);

        etBuscar = (EditText)view.findViewById(R.id.edtBuscar);
        btnBuscar = (Button)view.findViewById(R.id.btnBuscar);
        tlLista = (TableLayout)view.findViewById(R.id.tlLista);

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cargarTabla("https://inventario-pdm115.000webhostapp.com/ws_consulta_documentos.php");
            }
        });

        return view;

    }

    public void cargarTabla(String URL){
        /*String cadena = "Algebra";
        String cadena2 = "Ciencias";
        String cadena3 = "Ingles";

        TableRow row = new TableRow(getContext());
        TextView textView, textView2, textView3;
        for(int i=0;i<2;i++){
            textView = new TextView(getContext());
            textView.setGravity(Gravity.CENTER_VERTICAL);
            textView.setPadding(15,15,15,15);
            textView.setText(cadena);

            textView3 = new TextView(getContext());
            textView3.setGravity(Gravity.CENTER_VERTICAL);
            textView3.setPadding(15,15,15,15);
            textView3.setText(cadena2);

            textView2 = new TextView(getContext());
            textView2.setGravity(Gravity.CENTER_VERTICAL);
            textView2.setPadding(15,15,15,15);
            textView2.setText(cadena3);

            row.addView(textView);
            row.addView(textView2);
            row.addView(textView3);

        }
        tlLista.addView(row);*/

        //String URL = "";

        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                response = response.replace("][",",");
                if(response.length()>0){
                    try{
                        JSONArray doc = new JSONArray(response);
                        Log.i("sizejson",""+doc.length());

                        ArrayList<String> docu = new ArrayList<>();

                        for(int i = 0;i<doc.length(); i+=13){
                            try{
                                docu.add(doc.getString(i)+" "+doc.getString(i+1)+" "+doc.getString(i+2)+" "+doc.getString(i+3)+" "+doc.getString(i+4)+" "+doc.getString(i+5)+" "+doc.getString(i+6)+" "+doc.getString(i+7)+" "+doc.getString(i+8)+" "+doc.getString(i+9)+" "+doc.getString(i+10)+" "+doc.getString(i+11)+" "+doc.getString(i+12));
                            }catch (JSONException e){
                                e.printStackTrace();
                            }
                        }
                        String nombre, autor;
                        nombre = docu.get()

                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(stringRequest);

    }
}
