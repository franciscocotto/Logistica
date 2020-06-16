package com.example.logistica.ui.ruta;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.logistica.MapsActivity;
import com.example.logistica.R;

public class RutaFragment extends Fragment {

    public RutaFragment() {
    }

    private RutaViewModel documentosViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

       final  View view = inflater.inflate(R.layout.fragment_ruta, container, false);
        ImageButton add = (ImageButton) view.findViewById(R.id.agregarRuta);
        ImageButton editar = (ImageButton) view.findViewById(R.id.editarRuta);
        ImageButton eliminar = (ImageButton) view.findViewById(R.id.eliminarRuta);

        //open fragment addDocuments
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addRuta addruta = new addRuta();
                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.nav_host_fragment, new addRuta());
                fr.commit();
           /* startActivity(new Intent(getContext(), MapsActivity.class));*/
            }
        });
        editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /*startActivity(new Intent(getContext(), YourClass.class));*/
            }
        });
        eliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                /*startActivity(new Intent(getContext(), YourClass.class));*/
            }
        });


        return view;
    }
}
