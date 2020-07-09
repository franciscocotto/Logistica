package com.example.logistica.ui.conductor;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.logistica.Driver;
import com.example.logistica.R;
import com.example.logistica.ui.viajes.ConsultaViajes;

public class Conductor_Asignado extends Fragment {

    public Conductor_Asignado() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_despachador, container, false);
        ImageButton viaje = (ImageButton) view.findViewById(R.id.viewruta);
        viaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConsultaViajes consultaViajes = new ConsultaViajes();
                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.nav_host_fragment, new ConsultaViajes());
                fr.commit();
                ((Driver) getActivity()).getSupportActionBar().setTitle("Viajes");
            }
        });

        return view;
    }
}
