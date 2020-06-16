package com.example.logistica.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.logistica.Administrador;
import com.example.logistica.R;
import com.example.logistica.ui.ruta.RutaFragment;

public class HomeFragment extends Fragment {

    public HomeFragment() {
    }


    private HomeViewModel homeViewModel;

    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        final View view = inflater.inflate(R.layout.fragment_home, container, false);
     ImageButton ruta = (ImageButton) view.findViewById(R.id.addruta);
        //open fragment documentos
        ruta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                RutaFragment rutaFragment = new RutaFragment();
                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.nav_host_fragment, new RutaFragment());
                fr.commit();
                ((Administrador) getActivity()).getSupportActionBar().setTitle("Rutas");

            }
        });

        return view;
    }


}
