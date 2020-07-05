package com.example.logistica.ui.reportes;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.logistica.R;

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


public class Reporte extends Fragment{

    public Reporte() {
        // Required empty public constructor
    }

    Button btnReporteViajes;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_reporte, container, false);

        btnReporteViajes = (Button)view.findViewById(R.id.btnReporteViajes);

        btnReporteViajes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generarReporteViajes();
            }
        });
        return view;
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

        File file = new File(getContext().getExternalFilesDir(null), "usuarios.xls");
        FileOutputStream outputStream = null;

        try {
            outputStream = new FileOutputStream(file);
            workbook.write(outputStream);
            Toast.makeText(getContext(), "Ok", Toast.LENGTH_LONG).show();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}