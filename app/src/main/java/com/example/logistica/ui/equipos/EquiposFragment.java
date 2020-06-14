package com.example.logistica.ui.equipos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.logistica.AddEquipos;
import com.example.logistica.ConEquipos;
import com.example.logistica.PreEquipos;
import com.example.logistica.R;

public class EquiposFragment extends Fragment {

    private EquiposViewModel equiposViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

     final  View view = inflater.inflate(R.layout.fragment_equipos, container, false);

        ImageButton add = (ImageButton) view.findViewById(R.id.add_equi);
        ImageButton consult = (ImageButton) view.findViewById(R.id.consult_equi);
        ImageButton prestamo = (ImageButton) view.findViewById(R.id.prest_equi);

        //open fragment addDocuments
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AddEquipos addEquipos = new AddEquipos();
                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.nav_host_fragment, new AddEquipos());
                fr.commit();
                /*startActivity(new Intent(getContext(), YourClass.class));*/
            }
        });
        consult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ConEquipos conEquipos = new ConEquipos();
                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.nav_host_fragment, new ConEquipos());
                fr.commit();
                /*startActivity(new Intent(getContext(), YourClass.class));*/
            }
        });
        prestamo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PreEquipos preEquipos = new PreEquipos();
                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.nav_host_fragment, new PreEquipos());
                fr.commit();
                /*startActivity(new Intent(getContext(), YourClass.class));*/
            }
        });
        return view;
    }
}
