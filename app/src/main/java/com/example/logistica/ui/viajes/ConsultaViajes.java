package com.example.logistica.ui.viajes;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.logistica.Administrador;
import com.example.logistica.R;

public class ConsultaViajes extends Fragment {


    public ConsultaViajes() {
        // Required empty public constructor
    }

    ImageButton btnIngresarViaje;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_consulta_viajes, container, false);

        btnIngresarViaje = (ImageButton) view.findViewById(R.id.agregarViaje);

        btnIngresarViaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IngresarViaje ingresarViaje = new IngresarViaje();
                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.nav_host_fragment, new IngresarViaje());
                fr.commit();
                ((Administrador) getActivity()).getSupportActionBar().setTitle("Ingresar viaje");
            }
        });

        return view;
    }
}