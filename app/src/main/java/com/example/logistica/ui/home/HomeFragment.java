package com.example.logistica.ui.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.logistica.Administrador;
import com.example.logistica.R;
import com.example.logistica.ui.driver.Conductor;
import com.example.logistica.ui.driver.consultarConductor;
import com.example.logistica.ui.driver.opcionesConductor;
import com.example.logistica.ui.reportes.GenerarReporte;
import com.example.logistica.ui.ruta.ConsultaRutas;
import com.example.logistica.ui.viajes.ConsultaViajes;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class HomeFragment extends Fragment {

    public HomeFragment() {
    }

    private boolean res = false;
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
                    consultarConductor consultarConductor = new consultarConductor();
                    FragmentTransaction fr = getFragmentManager().beginTransaction();
                    fr.replace(R.id.nav_host_fragment, new consultarConductor());
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
                if(validaPermisos()){
                    GenerarReporte generarReporte = new GenerarReporte();
                    FragmentTransaction fr = getFragmentManager().beginTransaction();
                    fr.replace(R.id.nav_host_fragment, new GenerarReporte());
                    fr.commit();
                    ((Administrador) getActivity()).getSupportActionBar().setTitle("Reportes");
                }

            }
        });

        return view;
    }
    private boolean validaPermisos(){
        if(Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
            return true;
        }
        if((getContext().checkSelfPermission(WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED)&&
                (getContext().checkSelfPermission(READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED)){
            return true;
        }
        if((shouldShowRequestPermissionRationale(WRITE_EXTERNAL_STORAGE))||(shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE))){
            cargarDialogoRecomendacion();
        }
        else {
            requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE},100);
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }

    private void cargarDialogoRecomendacion(){
        AlertDialog.Builder dialogo = new AlertDialog.Builder(getContext());
        dialogo.setTitle("Permisos desactivados");
        dialogo.setMessage("Debe aceptar los permisos para poder generar reportes");
        dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestPermissions(new String[]{WRITE_EXTERNAL_STORAGE,READ_EXTERNAL_STORAGE},100);
            }
        });
        dialogo.show();
    }


}
