package com.example.logistica.ui.driver;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.logistica.R;

import java.util.ArrayList;

public class opcionesConductor extends Fragment implements AdapterView.OnItemSelectedListener {

    public opcionesConductor(){

    }

    Button  btnBuscarConductor,btnAddRegistro;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*Asociamos la clase actual al layout fragment_con_documentos
        Cabe destacar que se maneja como una vista, con el objetivo de realizar la carga del fragment
        dentro del Contentedor principal Conductores */
        View view = inflater.inflate(R.layout.fragment_opcion_conductor, container, false);
        btnBuscarConductor = (Button) view.findViewById(R.id.btnBuscar);
        btnAddRegistro = (Button) view.findViewById(R.id.btnAgregarCond);

        view.findViewById(R.id.btnBuscar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    consultarConductor buscarConductor = new consultarConductor();
                    FragmentTransaction fr = getFragmentManager().beginTransaction();
                    fr.replace(R.id.nav_host_fragment, new consultarConductor());
                    fr.commit();
                }catch (Exception e){
                    Toast.makeText(getContext(),"Ha ocurrido un error",Toast.LENGTH_SHORT);
                }
            }
        });

        view.findViewById(R.id.btnAgregarCond).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Conductor conductor = new Conductor();
                    FragmentTransaction fr = getFragmentManager().beginTransaction();
                    fr.replace(R.id.nav_host_fragment, new Conductor());
                    fr.commit();
                }catch (Exception e){
                    Toast.makeText(getContext(),"Ha ocurrido un error",Toast.LENGTH_SHORT);
                }
            }
        });


        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
