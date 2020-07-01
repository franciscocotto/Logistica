package com.example.logistica.ui.viajes;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
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
import com.example.logistica.Conductor;
import com.example.logistica.R;
import com.example.logistica.Rutas;
import com.example.logistica.Vehiculos;
import com.example.logistica.dialog.DatePickerFragment;
import com.example.logistica.dialog.TimePickerFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class IngresarViaje extends Fragment {

    //ArrayList
    ArrayList<Rutas> rutas = new ArrayList<Rutas>();
    ArrayList<Vehiculos> vehiculos = new ArrayList<Vehiculos>();

    //Vectores
    String[] rutasV;

    private AutoCompleteTextView acRutas;
    private EditText etFechaInicio, etHoraInicio, etFechaFinal, etHoraFinal;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_ingresar_viaje, container, false);

        acRutas = (AutoCompleteTextView)view.findViewById(R.id.acRutas);

        etFechaInicio = (EditText)view.findViewById(R.id.etFechaInicio);
        etFechaFinal = (EditText)view.findViewById(R.id.etFechaFinal);
        etHoraInicio = (EditText)view.findViewById(R.id.etHoraInicio);
        etHoraFinal = (EditText)view.findViewById(R.id.etHoraFinal);

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

    private void cargarComplementos(String URL, final int accion){
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity().getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.DEPRECATED_GET_OR_POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray consulta = new JSONArray(response);

                    switch (accion){
                        case 1:
                            //Cargar rutas almacenadas
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
                //parametros.put("campo", id);
                return parametros;
            }
        };
        requestQueue.add(stringRequest);
    }

    //Metodo que carga las rutas almacenadas
    private void cargarRutas(ArrayList list){

        this.rutas=list;
        rutasV = new String[rutas.size()];

        for (int i = 0; i<rutas.size(); i++){
            rutasV[i] = rutas.get(i).getNameruta();
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, rutasV);
        acRutas.setAdapter(adapter);
    }

}
