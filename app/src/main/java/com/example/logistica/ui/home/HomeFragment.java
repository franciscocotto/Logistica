package com.example.logistica.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.logistica.Administrador;
import com.example.logistica.R;
import com.example.logistica.ui.driver.opcionesConductor;
import com.example.logistica.ui.reportes.GenerarReporte;
import com.example.logistica.ui.ruta.ConsultaRutas;
import com.example.logistica.ui.viajes.ConsultaViajes;

public class HomeFragment extends Fragment {

    public HomeFragment() {
    }


    private HomeViewModel homeViewModel;

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        final View view = inflater.inflate(R.layout.fragment_home, container, false);
        ImageButton ruta = (ImageButton) view.findViewById(R.id.addruta);
        ImageButton conductor = (ImageButton) view.findViewById(R.id.btnConductor);
        ImageButton viaje = (ImageButton) view.findViewById(R.id.btnViajes);
        ImageButton reporte = (ImageButton) view.findViewById(R.id.btnReporte);
        //open fragment documentos
        ruta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ConsultaRutas consultaRutas = new ConsultaRutas();
                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.nav_host_fragment, new ConsultaRutas());
                fr.commit();
                ((Administrador) getActivity()).getSupportActionBar().setTitle("Rutas");

            }
        });

        conductor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    opcionesConductor opConductor = new opcionesConductor();
                    FragmentTransaction fr = getFragmentManager().beginTransaction();
                    fr.replace(R.id.nav_host_fragment, new opcionesConductor());
                    fr.commit();
                    ((Administrador) getActivity()).getSupportActionBar().setTitle("Conductores");
                }catch (Exception e){
                    Toast.makeText(getContext(),"Ha ocurrido un error",Toast.LENGTH_SHORT);
                }
            }
        });

        viaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConsultaViajes consultaViajes = new ConsultaViajes();
                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.nav_host_fragment, new ConsultaViajes());
                fr.commit();
                ((Administrador) getActivity()).getSupportActionBar().setTitle("Viajes");
            }
        });

        reporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GenerarReporte generarReporte = new GenerarReporte();
                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.nav_host_fragment, new GenerarReporte());
                fr.commit();
                ((Administrador) getActivity()).getSupportActionBar().setTitle("Reportes");
            }
        });

        return view;
    }


}
