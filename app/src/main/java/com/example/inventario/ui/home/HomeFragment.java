package com.example.inventario.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.inventario.Home;
import com.example.inventario.R;
import com.example.inventario.ui.documentos.DocumentosFragment;
import com.example.inventario.ui.equipos.EquiposFragment;

public class HomeFragment extends Fragment {

    public HomeFragment() {
    }


    private HomeViewModel homeViewModel;

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        final View view = inflater.inflate(R.layout.fragment_home, container, false);
        ImageButton document = (ImageButton) view.findViewById(R.id.btn_doc);
        ImageButton equip = (ImageButton) view.findViewById(R.id.btn_equi);
        //open fragment documentos
        document.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DocumentosFragment DocumentosFragment = new DocumentosFragment();
                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.nav_host_fragment, new DocumentosFragment());
                fr.commit();
                ((Home) getActivity()).getSupportActionBar().setTitle("Documentos");

            }
        });
        //open fragment equipos
        equip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EquiposFragment equiposFragment = new EquiposFragment();
                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.nav_host_fragment, new EquiposFragment());
                fr.commit();
                ((Home) getActivity()).getSupportActionBar().setTitle("Equipos");

            }
        });

        return view;
    }


}
