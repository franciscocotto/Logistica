package com.example.logistica.ui.reportes;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.logistica.Administrador;
import com.example.logistica.R;
import com.example.logistica.Reporte;
import com.example.logistica.ui.home.HomeFragment;
import com.example.logistica.ui.viajes.ConsultaViajes;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


public class GenerarReporte extends Fragment{

    public GenerarReporte() { }
    int posicion;
    String nombre;
    ArrayList<Reporte> reportes = new ArrayList<Reporte>();
    File carpeta = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "Logistica");
    ArrayList<String> rutasArchivos;
    ArrayList<String> nombreArchivos;
    ArrayList<Uri> URIS;
    String URLViajes = "https://inventario-pdm115.000webhostapp.com/Logistica/ws_bg17016/ws_reporte_viajes.php";

    ListView lvArchivos;
    Button btnReporteViajes, btnRegresar, btnAbrir, btnEnviar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_reporte, container, false);
        lvArchivos = (ListView)view.findViewById(R.id.lvArchivos);

        final EditText etNom = (EditText)view.findViewById(R.id.etNomReporte);
        btnReporteViajes = (Button)view.findViewById(R.id.btnReporteViajes);
        btnRegresar = (Button)view.findViewById(R.id.btnRegresar);
        btnAbrir = (Button)view.findViewById(R.id.btnAbrir);
        btnEnviar = (Button)view.findViewById(R.id.btnEnviar);
        cargarArchivos();
        btnReporteViajes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etNom.getText().toString().isEmpty()){
                    Toast.makeText(getContext(), "Ingrese el nombre del archivo", Toast.LENGTH_SHORT).show();
                }else {
                    nombre = etNom.getText().toString();
                    datosReporteViajes(URLViajes);
                }

            }
        });
        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regresar();
            }
        });
        lvArchivos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for(int i = 0; i<nombreArchivos.size();i++){
                    lvArchivos.getChildAt(i).setBackgroundColor(Color.WHITE);
                }
                lvArchivos.getChildAt(position).setBackgroundColor(Color.GRAY);
                posicion = position;
                btnAbrir.setVisibility(View.VISIBLE);
                btnEnviar.setVisibility(View.VISIBLE);
            }
        });

        btnAbrir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                abrirReporteExel(URIS.get(posicion));
            }
        });

        btnEnviar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarReporte(URIS.get(posicion));
            }
        });
        return view;
    }
    private void datosReporteViajes(String URL){
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.DEPRECATED_GET_OR_POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if(!response.isEmpty()){
                    try {
                        JSONArray consulta = new JSONArray(response);
                        ArrayList<Reporte> reportes = new ArrayList<Reporte>();
                        for (int i = 0; i<consulta.length(); i++){
                            JSONObject object = consulta.getJSONObject(i);
                            reportes.add(new Reporte(
                                    object.getInt("id_viaje"),
                                    object.getString("nameruta"),
                                    object.getString("origen"),
                                    object.getString("destino"),
                                    object.getString("cod_vehiculo"),
                                    object.getString("placa"),
                                    object.getString("dui"),
                                    object.getString("nombre"),
                                    object.getString("apellido"),
                                    object.getString("licencia"),
                                    object.getString("inicio"),
                                    object.getString("final"),
                                    object.getString("nom_viaje")));
                        }
                        generarReporteViajes(reportes);
                    }
                    catch (JSONException e){
                        Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(getContext(), "TABLA VIAJES VACIA...", Toast.LENGTH_SHORT).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(stringRequest);
    }


    private void abrirReporteExel(Uri uri){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/vnd.ms-excel");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        try {
            this.startActivity(intent);
        }catch (ActivityNotFoundException e){
            e.printStackTrace();
        }


    }
    private void enviarReporte(Uri uri){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/vnd.ms-excel");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        startActivity(intent);
    }

    private void generarReporteViajes(ArrayList list){
        this.reportes = list;
        HSSFWorkbook reporteViajes = new HSSFWorkbook();
        Cell celda = null;
        Sheet sheet = null;
        Row fila = null;

        CellStyle estiloCelda = reporteViajes.createCellStyle();
        estiloCelda.setFillForegroundColor(HSSFColor.LIGHT_BLUE.index);
        estiloCelda.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        sheet = reporteViajes.createSheet("Reporte de viajes");

        fila = sheet.createRow(0);
        celda = fila.createCell(0);
        celda.setCellValue("ID VIAJE");
        celda.setCellStyle(estiloCelda);
        sheet.setColumnWidth(0, 2500);

        sheet.createRow(1);
        celda = fila.createCell(1);
        celda.setCellValue("NOMBRE DEL VIAJE");
        celda.setCellStyle(estiloCelda);
        sheet.setColumnWidth(1, 9000);

        sheet.createRow(2);
        celda = fila.createCell(2);
        celda.setCellValue("NOMBRE DE LA RUTA");
        celda.setCellStyle(estiloCelda);
        sheet.setColumnWidth(2, 9000);

        sheet.createRow(3);
        celda = fila.createCell(3);
        celda.setCellValue("ORIGEN DE RUTA");
        celda.setCellStyle(estiloCelda);
        sheet.setColumnWidth(3, 9000);

        sheet.createRow(4);
        celda = fila.createCell(4);
        celda.setCellValue("DESTINO DE RUTA");
        celda.setCellStyle(estiloCelda);
        sheet.setColumnWidth(4, 9000);

        sheet.createRow(5);
        celda = fila.createCell(5);
        celda.setCellValue("CODIGO DE VEHICULO");
        celda.setCellStyle(estiloCelda);
        sheet.setColumnWidth(5, 5500);

        sheet.createRow(6);
        celda = fila.createCell(6);
        celda.setCellValue("PLACA DEL VEHICULO");
        celda.setCellStyle(estiloCelda);
        sheet.setColumnWidth(6, 5500);

        sheet.createRow(7);
        celda = fila.createCell(7);
        celda.setCellValue("DUI DEL CONDUCTOR");
        celda.setCellStyle(estiloCelda);
        sheet.setColumnWidth(7, 5500);

        sheet.createRow(8);
        celda = fila.createCell(8);
        celda.setCellValue("NOMBRE DEL CONDUCTOR");
        celda.setCellStyle(estiloCelda);
        sheet.setColumnWidth(8, 7000);

        sheet.createRow(9);
        celda = fila.createCell(9);
        celda.setCellValue("APELLIDO DEL CONDUCTOR");
        celda.setCellStyle(estiloCelda);
        sheet.setColumnWidth(9, 7000);

        sheet.createRow(10);
        celda = fila.createCell(10);
        celda.setCellValue("LICENCIA DEL CONDUCTOR");
        celda.setCellStyle(estiloCelda);
        sheet.setColumnWidth(10, 7000);

        sheet.createRow(11);
        celda = fila.createCell(11);
        celda.setCellValue("INICIO DEL VIAJE");
        celda.setCellStyle(estiloCelda);
        sheet.setColumnWidth(11, 6000);

        sheet.createRow(12);
        celda = fila.createCell(12);
        celda.setCellValue("FIN DEL VIAJE");
        celda.setCellStyle(estiloCelda);
        sheet.setColumnWidth(12, 6000);


        for (int i = 0; i<reportes.size();i++){
            fila = sheet.createRow(i+1);

            celda = fila.createCell(0);
            celda.setCellValue(reportes.get(i).getId_viaje());
            celda = fila.createCell(1);
            celda.setCellValue(reportes.get(i).getNombreViaje());
            celda = fila.createCell(2);
            celda.setCellValue(reportes.get(i).getNombreRuta());
            celda = fila.createCell(3);
            celda.setCellValue(reportes.get(i).getOrigenRuta());
            celda = fila.createCell(4);
            celda.setCellValue(reportes.get(i).getDestinoRuta());
            celda = fila.createCell(5);
            celda.setCellValue(reportes.get(i).getCodVehiculo());
            celda = fila.createCell(6);
            celda.setCellValue(reportes.get(i).getPlacaVehiculo());
            celda = fila.createCell(7);
            celda.setCellValue(reportes.get(i).getDuiConductor());
            celda = fila.createCell(8);
            celda.setCellValue(reportes.get(i).getNombreConductor());
            celda = fila.createCell(9);
            celda.setCellValue(reportes.get(i).getApellidoConductor());
            celda = fila.createCell(10);
            celda.setCellValue(reportes.get(i).getLicenciaConductor());
            celda = fila.createCell(11);
            celda.setCellValue(reportes.get(i).getInicioViaje());
            celda = fila.createCell(12);
            celda.setCellValue(reportes.get(i).getFinalViaje());
        }

        File file = new File(carpeta.getPath(), nombre+".xls");
        FileOutputStream outputStream = null;

        try {
            file.createNewFile();
            outputStream = new FileOutputStream(file);
            reporteViajes.write(outputStream);
            cargarArchivos();
            Toast.makeText(getContext(), "Reporte creado con exito", Toast.LENGTH_LONG).show();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
    private void cargarArchivos(){
        btnEnviar.setVisibility(View.GONE);
        btnAbrir.setVisibility(View.GONE);
        rutasArchivos = new ArrayList<String>();
        nombreArchivos = new ArrayList<String>();
        URIS = new ArrayList<>();

        if(!carpeta.exists()){
            carpeta.mkdirs();
        }

        File[] listaArchivos = carpeta.listFiles();

        for (File archivo : listaArchivos){
            rutasArchivos.add(archivo.getPath());
            URIS.add(FileProvider.getUriForFile(getContext(), getActivity().getOpPackageName()+".provider", archivo));
        }

        for (int i = 0; i<listaArchivos.length; i++){
            File archivo = new File(rutasArchivos.get(i));
            nombreArchivos.add(archivo.getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, nombreArchivos);
        lvArchivos.setAdapter(adapter);
        updateListViewHeight(lvArchivos);
    }
    public static void updateListViewHeight(ListView lista) {
        ListAdapter myListAdapter = lista.getAdapter();
        if (myListAdapter == null) {
            return;
        }
        // get listview height
        int totalHeight = 0;
        int adapterCount = myListAdapter.getCount();
        for (int size = 0; size < adapterCount; size++) {
            View listItem = myListAdapter.getView(size, null, lista);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        // Change Height of ListView
        ViewGroup.LayoutParams params = lista.getLayoutParams();
        params.height = (totalHeight
                + (lista.getDividerHeight() * (adapterCount)));
        lista.setLayoutParams(params);
    }
    private void regresar(){
        HomeFragment homeFragment = new HomeFragment();
        FragmentTransaction fr = getFragmentManager().beginTransaction();
        fr.replace(R.id.nav_host_fragment, new HomeFragment());
        fr.commit();
    }
}