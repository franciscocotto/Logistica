package com.example.logistica;

public class Conductores {
    private int id_conductor;
    private String dui, nombre, apellido, telefono, url_foto, tipo_licencia;

    public Conductores(){}

    public Conductores(int id_conductor, String dui, String nombre, String apellido, String telefono, String url_foto, String tipo_licencia) {
        this.dui = dui;
        this.id_conductor = id_conductor;
        this.nombre = nombre;
        this.apellido = apellido;
        this.telefono = telefono;
        this.url_foto = url_foto;
        this.tipo_licencia = tipo_licencia;
    }

    public int getId_conductor() {
        return id_conductor;
    }

    public String getDui() {
        return dui;
    }

    public String getNombre() {
        return nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getUrl_foto() {
        return url_foto;
    }

    public String getTipo_licencia() {
        return tipo_licencia;
    }


    public void setId_conductor(int id_conductor) {
        this.id_conductor = id_conductor;
    }

    public void setDui(String dui) {
        this.dui = dui;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setUrl_foto(String url_foto) {
        this.url_foto = url_foto;
    }

    public void setTipo_licencia(String tipo_licencia) {
        this.tipo_licencia = tipo_licencia;
    }
}
