package com.example.logistica.ui.reportes;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.logistica.R;
import com.example.logistica.Reporte;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


public class GenerarReporte extends Fragment{

    public GenerarReporte() { }

    ArrayList<Reporte> reportes = new ArrayList<Reporte>();

    String URLViajes = "https://inventario-pdm115.000webhostapp.com/Logistica/ws_bg17016/ws_reporte_viajes.php";

    Button btnReporteViajes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_reporte, container, false);

        cargarArchivos();

        btnReporteViajes = (Button)view.findViewById(R.id.btnReporteViajes);

        btnReporteViajes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generarReporteViajes();
            }
        });
        return view;
    }
    private void datosReporteViajes(String URL){
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.DEPRECATED_GET_OR_POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
    }

    private void cargarArchivos(){
        ArrayList<String> rutasArchivos = new ArrayList<String>();
        ArrayList<String> nombreArchivos = new ArrayList<String>();
        File directorioActual = new File(String.valueOf(getContext().getExternalFilesDir(null)));
        File[] listaArchivos = directorioActual.listFiles();

        for (File archivo : listaArchivos){
            rutasArchivos.add(archivo.getPath());
        }

        for (int i = 0; i<listaArchivos.length; i++){
            File archivo = new File(rutasArchivos.get(i));
            nombreArchivos.add(archivo.getName());
        }
    }

    private void generarReporteViajes(){
        Workbook workbook = new HSSFWorkbook();
        Cell cell = null;
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFillForegroundColor(HSSFColor.LIGHT_BLUE.index);
        cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        Sheet sheet = null;
        sheet = workbook.createSheet("Lista de usuarios");

        Row row = null;
        row = sheet.createRow(0);
        cell = row.createCell(0);
        cell.setCellValue("USUARIO");
        cell.setCellStyle(cellStyle);

        sheet.createRow(1);
        cell = row.createCell(1);
        cell.setCellValue("NOMBRE");
        cell.setCellStyle(cellStyle);

        row = sheet.createRow(1);
        cell = row.createCell(0);
        cell.setCellValue("xcheko51.x");

        cell = row.createCell(1);
        cell.setCellValue("Sergio");

        File file = new File(getContext().getExternalFilesDir(null), "usua3.xls");

        FileOutputStream outputStream = null;

        try {
            file.createNewFile();
            outputStream = new FileOutputStream(file);
            workbook.write(outputStream);
            Toast.makeText(getContext(), "Ok", Toast.LENGTH_LONG).show();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}