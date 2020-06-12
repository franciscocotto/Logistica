package com.example.inventario.ui.documentos;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.inventario.AddDocumentos;
import com.example.inventario.BuscarDocumento;
import com.example.inventario.ConDocumentos;
import com.example.inventario.PreDocumentos;
import com.example.inventario.R;

public class DocumentosFragment extends Fragment {

    public DocumentosFragment() {
    }

    private DocumentosViewModel documentosViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

       final  View view = inflater.inflate(R.layout.fragment_documentos, container, false);
        ImageButton add = (ImageButton) view.findViewById(R.id.add_doc);
        ImageButton consult = (ImageButton) view.findViewById(R.id.consult_doc);
        ImageButton prestamo = (ImageButton) view.findViewById(R.id.prest_doc);

        //open fragment addDocuments
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AddDocumentos addDocumentos = new AddDocumentos();
                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.nav_host_fragment, new AddDocumentos());
                fr.commit();
              /*startActivity(new Intent(getContext(), YourClass.class));*/
            }
        });
        consult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ConDocumentos conDocumentos = new ConDocumentos();
                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.nav_host_fragment, new ConDocumentos());
                fr.commit();
                /*startActivity(new Intent(getContext(), YourClass.class));*/
            }
        });
        prestamo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                BuscarDocumento buscarDocumentos = new BuscarDocumento();
                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.nav_host_fragment, new BuscarDocumento());
                fr.commit();
                /*startActivity(new Intent(getContext(), YourClass.class));*/
            }
        });


        return view;
    }
}
