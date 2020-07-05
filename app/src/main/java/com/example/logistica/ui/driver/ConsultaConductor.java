package com.example.logistica.ui.driver;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.logistica.R;

public class ConsultaConductor {

    private String id_conductor;
    private String  dui;
    private String nombre;
    private String  apellido;
    private String  nit;
    private String  telefono;
    private String  direccion;
    private String  url_foto;
    private String  licencia;
    private String  tipo_licencia;
    protected static String  idConductorAux;
    protected static String  hintEdit;

    public ConsultaConductor(){

    }

    public ConsultaConductor(String id_conductor,
                             String  dui,
                             String  nombre,
                             String  apellido,
                             String  nit,
                             String  telefono,
                             String  direccion,
                             String  url_foto,
                             String  licencia,
                             String  tipo_licencia) {
        this.id_conductor = id_conductor;
        this.dui = dui;
        this.nombre= nombre;
        this.apellido= apellido;
        this.nit = nit;
        this.telefono = telefono;
        this.direccion = direccion;
        this.url_foto = url_foto;
        this.licencia = licencia;
        this.tipo_licencia = tipo_licencia;
    }

    public String getId_conductor() {
        return id_conductor;
    }

    public void setId_conductor(String id_conductor) {
        this.id_conductor = id_conductor;
    }

    public String getDui() {
        return dui;
    }

    public void setDui(String dui) {
        this.dui = dui;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getUrl_foto() {
        return url_foto;
    }

    public void setUrl_foto(String url_foto) {
        this.url_foto = url_foto;
    }

    public String getLicencia() {
        return licencia;
    }

    public void setLicencia(String licencia) {
        this.licencia = licencia;
    }

    public String getTipo_licencia() {
        return tipo_licencia;
    }

    public void setTipo_licencia(String tipo_licencia) {
        this.tipo_licencia = tipo_licencia;
    }

    public static void setIdConductorAux(String aux){
        idConductorAux = aux;
    }

    public static String getIdConductorAux() {
        return idConductorAux;
    }

    public static void sethintEdit(String aux){
        hintEdit = aux;
    }

    public static String gethintEdit() {
        return hintEdit;
    }


}